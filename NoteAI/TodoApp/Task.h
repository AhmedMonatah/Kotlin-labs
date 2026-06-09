// Task.h
#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, TaskPriority) {
    TaskPriorityLow = 0,
    TaskPriorityMedium,
    TaskPriorityHigh
};

typedef NS_ENUM(NSInteger, TaskStatus) {
    TaskStatusTodo = 0,
    TaskStatusInProgress,
    TaskStatusDone
};

@interface Task : NSObject <NSCoding>

@property (nonatomic, strong) NSString *taskID;
@property (nonatomic, strong) NSString *title;
@property (nonatomic, strong) NSString *taskDescription;
@property (nonatomic, assign) TaskPriority priority;
@property (nonatomic, assign) TaskStatus status;
@property (nonatomic, strong) NSDate *dueDate;
@property (nonatomic, strong) NSDate *createdAt;

- (instancetype)initWithTitle:(NSString *)title
                  description:(NSString *)description
                     priority:(TaskPriority)priority
                       status:(TaskStatus)status
                      dueDate:(NSDate *)dueDate;

- (NSString *)priorityString;
- (NSString *)statusString;

@end
