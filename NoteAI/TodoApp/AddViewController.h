// AddViewController.h
#import <UIKit/UIKit.h>
#import "Task.h"

@interface AddViewController : UIViewController

@property (nonatomic, strong) Task *taskToEdit;

@property (weak, nonatomic) IBOutlet UITextField *titleField;
@property (weak, nonatomic) IBOutlet UITextView *descView;
@property (weak, nonatomic) IBOutlet UISegmentedControl *priorityControl;
@property (weak, nonatomic) IBOutlet UISegmentedControl *statusControl;
@property (weak, nonatomic) IBOutlet UIDatePicker *datePicker;

- (IBAction)saveClicked:(id)sender;
- (IBAction)cancelClicked:(id)sender;

@end
