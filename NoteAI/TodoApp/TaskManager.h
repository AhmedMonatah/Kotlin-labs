// TaskManager.h
#import <Foundation/Foundation.h>
#import "Task.h"

extern NSString *const TaskManagerDidUpdateNotification;

@interface TaskManager : NSObject

+ (instancetype)sharedManager;

@property (nonatomic, readonly) NSArray<Task *> *allTasks;
@property (nonatomic, readonly) NSArray<Task *> *todoTasks;
@property (nonatomic, readonly) NSArray<Task *> *inProgressTasks;
@property (nonatomic, readonly) NSArray<Task *> *doneTasks;

- (void)addTask:(Task *)task;
- (void)updateTask:(Task *)task;
- (void)deleteTask:(Task *)task;
- (void)deleteTaskWithID:(NSString *)taskID;

@end
