// AddViewController.m
#import "AddViewController.h"
#import "TaskManager.h"

@implementation AddViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self applyTheme];

    if (self.taskToEdit) {
        self.title = @"Edit Task";
        [self populateFields];
    } else {
        self.title = @"New Task";
        self.statusControl.enabled = NO; 
    }
}

- (void)applyTheme {
    UIColor *bgColor = [UIColor colorWithRed:0.12 green:0.12 blue:0.13 alpha:1.0];
    self.view.backgroundColor = bgColor;
    
    // Style the navigation bar for this modal
    self.navigationController.navigationBar.barTintColor = bgColor;
    self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName: [UIColor whiteColor]};
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];

    // Style the text field
    self.titleField.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.05];
    self.titleField.textColor = [UIColor whiteColor];
    self.titleField.layer.cornerRadius = 15;
    self.titleField.layer.borderWidth = 0;
    self.titleField.clipsToBounds = YES;
    self.titleField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:self.titleField.placeholder ?: @"Task Title"
                                                                            attributes:@{NSForegroundColorAttributeName: [UIColor colorWithWhite:1.0 alpha:0.4]}];

    // Style the text view
    self.descView.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.05];
    self.descView.textColor = [UIColor whiteColor];
    self.descView.layer.cornerRadius = 15;
    self.descView.layer.borderWidth = 0;
    self.descView.clipsToBounds = YES;

    // Segmented controls
    UIColor *accent = [UIColor colorWithRed:0.424 green:0.388 blue:1.0 alpha:1.0];
    self.priorityControl.selectedSegmentTintColor = accent;
    self.priorityControl.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.05];
    NSDictionary *selectedAttrs = @{ NSForegroundColorAttributeName: [UIColor whiteColor] };
    NSDictionary *normalAttrs = @{ NSForegroundColorAttributeName: [UIColor colorWithWhite:1.0 alpha:0.6] };
    [self.priorityControl setTitleTextAttributes:selectedAttrs forState:UIControlStateSelected];
    [self.priorityControl setTitleTextAttributes:normalAttrs forState:UIControlStateNormal];

    self.statusControl.selectedSegmentTintColor = accent;
    self.statusControl.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.05];
    [self.statusControl setTitleTextAttributes:selectedAttrs forState:UIControlStateSelected];
    [self.statusControl setTitleTextAttributes:normalAttrs forState:UIControlStateNormal];

    // Date picker
    self.datePicker.tintColor = [UIColor whiteColor];
    if (@available(iOS 13.4, *)) {
        self.datePicker.preferredDatePickerStyle = UIDatePickerStyleCompact;
    }
}

- (void)populateFields {
    self.titleField.text = self.taskToEdit.title;
    self.descView.text = self.taskToEdit.taskDescription;
    self.priorityControl.selectedSegmentIndex = self.taskToEdit.priority;
    self.statusControl.selectedSegmentIndex = self.taskToEdit.status;
    self.datePicker.date = self.taskToEdit.dueDate;
}

#pragma mark - Actions

- (IBAction)saveClicked:(id)sender {
    NSString *title = [self.titleField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];

    if (title.length == 0) {
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Required Field"
                                                                        message:@"Please enter a title for your task."
                                                                 preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
        [self presentViewController:alert animated:YES completion:nil];
        return;
    }

    if (self.taskToEdit) {
        self.taskToEdit.title = title;
        self.taskToEdit.taskDescription = self.descView.text;
        self.taskToEdit.priority = self.priorityControl.selectedSegmentIndex;
        self.taskToEdit.status = self.statusControl.selectedSegmentIndex;
        self.taskToEdit.dueDate = self.datePicker.date;
        [[TaskManager sharedManager] updateTask:self.taskToEdit];
    } else {
        Task *newTask = [[Task alloc] initWithTitle:title
                                        description:self.descView.text
                                           priority:self.priorityControl.selectedSegmentIndex
                                             status:TaskStatusTodo
                                            dueDate:self.datePicker.date];
        [[TaskManager sharedManager] addTask:newTask];
    }

    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)cancelClicked:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
