// TaskManager.m
#import "TaskManager.h"
#import <UserNotifications/UserNotifications.h>

NSString *const TaskManagerDidUpdateNotification = @"TaskManagerDidUpdateNotification";
static NSString *const kTasksStorageKey = @"com.todoapp.tasks";

@interface TaskManager ()
@property (nonatomic, strong) NSMutableArray<Task *> *tasks;
@end

@implementation TaskManager

+ (instancetype)sharedManager {
    static TaskManager *shared = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        shared = [[TaskManager alloc] init];
    });
    return shared;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        [self loadTasks];
    }
    return self;
}

#pragma mark - Public Accessors

- (NSArray<Task *> *)allTasks {
    return [self.tasks sortedArrayUsingComparator:^NSComparisonResult(Task *a, Task *b) {
        return [b.createdAt compare:a.createdAt];
    }];
}

- (NSArray<Task *> *)todoTasks {
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"status == %d", TaskStatusTodo];
    NSArray *filtered = [self.tasks filteredArrayUsingPredicate:pred];
    return [filtered sortedArrayUsingComparator:^NSComparisonResult(Task *a, Task *b) {
        return [b.createdAt compare:a.createdAt];
    }];
}

- (NSArray<Task *> *)inProgressTasks {
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"status == %d", TaskStatusInProgress];
    NSArray *filtered = [self.tasks filteredArrayUsingPredicate:pred];
    return [filtered sortedArrayUsingComparator:^NSComparisonResult(Task *a, Task *b) {
        return [b.createdAt compare:a.createdAt];
    }];
}

- (NSArray<Task *> *)doneTasks {
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"status == %d", TaskStatusDone];
    NSArray *filtered = [self.tasks filteredArrayUsingPredicate:pred];
    return [filtered sortedArrayUsingComparator:^NSComparisonResult(Task *a, Task *b) {
        return [b.createdAt compare:a.createdAt];
    }];
}

#pragma mark - CRUD

- (void)addTask:(Task *)task {
    [self.tasks addObject:task];
    [self saveTasks];
    [self scheduleNotificationForTask:task];
    [self postUpdateNotification];
}

- (void)updateTask:(Task *)task {
    NSUInteger index = [self.tasks indexOfObjectPassingTest:^BOOL(Task *obj, NSUInteger idx, BOOL *stop) {
        return [obj.taskID isEqualToString:task.taskID];
    }];

    if (index != NSNotFound) {
        self.tasks[index] = task;
        [self saveTasks];
        
        if (task.status == TaskStatusTodo || task.status == TaskStatusInProgress) {
            [self scheduleNotificationForTask:task];
        } else {
            [self cancelNotificationForTask:task];
        }
        
        [self postUpdateNotification];
    }
}

- (void)deleteTask:(Task *)task {
    [self deleteTaskWithID:task.taskID];
}

- (void)deleteTaskWithID:(NSString *)taskID {
    [self cancelNotificationForTaskID:taskID];
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"taskID != %@", taskID];
    self.tasks = [[self.tasks filteredArrayUsingPredicate:pred] mutableCopy];
    [self saveTasks];
    [self postUpdateNotification];
}

#pragma mark - Notifications Logic

- (void)scheduleNotificationForTask:(Task *)task {
    if (task.status == TaskStatusDone || [task.dueDate timeIntervalSinceNow] <= 0) {
        return;
    }

    UNMutableNotificationContent *content = [[UNMutableNotificationContent alloc] init];
    content.title = @"Task Reminder";
    content.body = task.title;
    content.sound = [UNNotificationSound defaultSound];

    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *components = [calendar components:(NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay | NSCalendarUnitHour | NSCalendarUnitMinute) fromDate:task.dueDate];
    UNCalendarNotificationTrigger *trigger = [UNCalendarNotificationTrigger triggerWithDateMatchingComponents:components repeats:NO];

    UNNotificationRequest *request = [UNNotificationRequest requestWithIdentifier:task.taskID content:content trigger:trigger];
    [[UNUserNotificationCenter currentNotificationCenter] addNotificationRequest:request withCompletionHandler:^(NSError * _Nullable error) {
        if (error) {
            NSLog(@"Error scheduling notification: %@", error);
        }
    }];
}

- (void)cancelNotificationForTask:(Task *)task {
    [self cancelNotificationForTaskID:task.taskID];
}

- (void)cancelNotificationForTaskID:(NSString *)taskID {
    [[UNUserNotificationCenter currentNotificationCenter] removePendingNotificationRequestsWithIdentifiers:@[taskID]];
}

#pragma mark - Persistence

- (void)saveTasks {
    @try {
        NSData *data = [NSKeyedArchiver archivedDataWithRootObject:self.tasks];
        if (data) {
            [[NSUserDefaults standardUserDefaults] setObject:data forKey:kTasksStorageKey];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
    } @catch (NSException *exception) {
        NSLog(@"Failed to save tasks: %@", exception);
    }
}

- (void)loadTasks {
    NSData *data = [[NSUserDefaults standardUserDefaults] objectForKey:kTasksStorageKey];
    if (data) {
        @try {
            NSArray *loaded = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            self.tasks = loaded ? [loaded mutableCopy] : [NSMutableArray array];
            
            // Migration: old format had Done=1, new format has InProgress=1, Done=2
            // Check if migration is needed
            BOOL migrationDone = [[NSUserDefaults standardUserDefaults] boolForKey:@"com.todoapp.migrated_v2"];
            if (!migrationDone) {
                for (Task *task in self.tasks) {
                    // Old Done (1) becomes new Done (2)
                    if (task.status == TaskStatusInProgress) {
                        task.status = TaskStatusDone;
                    }
                }
                [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"com.todoapp.migrated_v2"];
                [self saveTasks];
            }
        } @catch (NSException *exception) {
            NSLog(@"Failed to load tasks: %@", exception);
            self.tasks = [NSMutableArray array];
        }
    } else {
        self.tasks = [NSMutableArray array];
    }
}

- (void)postUpdateNotification {
    [[NSNotificationCenter defaultCenter]
        postNotificationName:TaskManagerDidUpdateNotification
                      object:self];
}

@end
