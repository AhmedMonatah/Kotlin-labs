// Task.m
#import "Task.h"

@implementation Task

- (instancetype)initWithTitle:(NSString *)title
                  description:(NSString *)description
                     priority:(TaskPriority)priority
                       status:(TaskStatus)status
                      dueDate:(NSDate *)dueDate {
    self = [super init];
    if (self) {
        _taskID = [[NSUUID UUID] UUIDString];
        _title = title;
        _taskDescription = description;
        _priority = priority;
        _status = status;
        _dueDate = dueDate;
        _createdAt = [NSDate date];
    }
    return self;
}

- (NSString *)priorityString {
    switch (self.priority) {
        case TaskPriorityLow:    return @"Low";
        case TaskPriorityMedium: return @"Medium";
        case TaskPriorityHigh:   return @"High";
    }
}

- (NSString *)statusString {
    switch (self.status) {
        case TaskStatusTodo:       return @"To Do";
        case TaskStatusInProgress: return @"In Progress";
        case TaskStatusDone:       return @"Done";
    }
}

#pragma mark - NSCoding

- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.taskID forKey:@"taskID"];
    [coder encodeObject:self.title forKey:@"title"];
    [coder encodeObject:self.taskDescription forKey:@"taskDescription"];
    [coder encodeInteger:self.priority forKey:@"priority"];
    [coder encodeInteger:self.status forKey:@"status"];
    [coder encodeObject:self.dueDate forKey:@"dueDate"];
    [coder encodeObject:self.createdAt forKey:@"createdAt"];
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _taskID = [coder decodeObjectForKey:@"taskID"];
        _title = [coder decodeObjectForKey:@"title"];
        _taskDescription = [coder decodeObjectForKey:@"taskDescription"];
        _priority = [coder decodeIntegerForKey:@"priority"];
        _status = [coder decodeIntegerForKey:@"status"];
        _dueDate = [coder decodeObjectForKey:@"dueDate"];
        _createdAt = [coder decodeObjectForKey:@"createdAt"];
    }
    return self;
}

@end
