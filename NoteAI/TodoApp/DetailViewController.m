// DetailViewController.m
#import "DetailViewController.h"
#import "TaskManager.h"
#import "AddViewController.h"

// ─── Theme color helpers ───
static UIColor *ThemeAccentColor(void) {
    return [UIColor colorWithRed:0.424 green:0.388 blue:1.0 alpha:1.0]; // #6C63FF
}
static UIColor *ThemeBackgroundColor(void) {
    return [UIColor colorWithRed:0.12 green:0.12 blue:0.13 alpha:1.0];
}

@interface DetailViewController ()
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *cardView;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UILabel *descLabel;
@property (nonatomic, strong) UILabel *priorityBadge;
@property (nonatomic, strong) UILabel *statusBadge;
@property (nonatomic, strong) UILabel *dueDateLabel;
@property (nonatomic, strong) UILabel *createdDateLabel;
@property (nonatomic, strong) UIStackView *actionStack;
@end

@implementation DetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Task Details";
    self.view.backgroundColor = ThemeBackgroundColor();
    
    // Style nav bar
    self.navigationController.navigationBar.barTintColor = ThemeBackgroundColor();
    self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName: [UIColor whiteColor]};
    self.navigationController.navigationBar.tintColor = [UIColor whiteColor];

    [self buildUI];
    [self populateData];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onTaskUpdated)
                                                 name:TaskManagerDidUpdateNotification
                                               object:nil];

    self.navigationItem.rightBarButtonItem =
        [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemEdit
                                                     target:self
                                                     action:@selector(editTapped)];
}

#pragma mark - Build UI Programmatically

- (void)buildUI {
    self.scrollView = [[UIScrollView alloc] init];
    self.scrollView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.scrollView];
    [NSLayoutConstraint activateConstraints:@[
        [self.scrollView.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor],
        [self.scrollView.leadingAnchor constraintEqualToAnchor:self.view.leadingAnchor],
        [self.scrollView.trailingAnchor constraintEqualToAnchor:self.view.trailingAnchor],
        [self.scrollView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor],
    ]];

    UIStackView *content = [[UIStackView alloc] init];
    content.axis = UILayoutConstraintAxisVertical;
    content.spacing = 20;
    content.translatesAutoresizingMaskIntoConstraints = NO;
    [self.scrollView addSubview:content];
    [NSLayoutConstraint activateConstraints:@[
        [content.topAnchor constraintEqualToAnchor:self.scrollView.topAnchor constant:20],
        [content.leadingAnchor constraintEqualToAnchor:self.view.leadingAnchor constant:20],
        [content.trailingAnchor constraintEqualToAnchor:self.view.trailingAnchor constant:-20],
        [content.bottomAnchor constraintEqualToAnchor:self.scrollView.bottomAnchor constant:-30],
    ]];

    self.cardView = [self makeCard];
    [content addArrangedSubview:self.cardView];

    UIStackView *cardStack = [[UIStackView alloc] init];
    cardStack.axis = UILayoutConstraintAxisVertical;
    cardStack.spacing = 14;
    cardStack.translatesAutoresizingMaskIntoConstraints = NO;
    [self.cardView addSubview:cardStack];
    [NSLayoutConstraint activateConstraints:@[
        [cardStack.topAnchor constraintEqualToAnchor:self.cardView.topAnchor constant:24],
        [cardStack.leadingAnchor constraintEqualToAnchor:self.cardView.leadingAnchor constant:20],
        [cardStack.trailingAnchor constraintEqualToAnchor:self.cardView.trailingAnchor constant:-20],
        [cardStack.bottomAnchor constraintEqualToAnchor:self.cardView.bottomAnchor constant:-24],
    ]];

    self.titleLabel = [[UILabel alloc] init];
    self.titleLabel.font = [UIFont systemFontOfSize:26 weight:UIFontWeightBold];
    self.titleLabel.textColor = [UIColor whiteColor];
    self.titleLabel.numberOfLines = 0;
    [cardStack addArrangedSubview:self.titleLabel];

    UIStackView *badgeRow = [[UIStackView alloc] init];
    badgeRow.axis = UILayoutConstraintAxisHorizontal;
    badgeRow.spacing = 10;
    badgeRow.alignment = UIStackViewAlignmentCenter;

    self.priorityBadge = [self makeBadge];
    self.statusBadge = [self makeBadge];
    [badgeRow addArrangedSubview:self.priorityBadge];
    [badgeRow addArrangedSubview:self.statusBadge];
    [badgeRow addArrangedSubview:[[UIView alloc] init]];
    [cardStack addArrangedSubview:badgeRow];

    UIView *sep = [[UIView alloc] init];
    sep.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.1];
    sep.translatesAutoresizingMaskIntoConstraints = NO;
    [sep.heightAnchor constraintEqualToConstant:1].active = YES;
    [cardStack addArrangedSubview:sep];

    UILabel *descHeader = [[UILabel alloc] init];
    descHeader.text = @"Description";
    descHeader.font = [UIFont systemFontOfSize:13 weight:UIFontWeightSemibold];
    descHeader.textColor = [UIColor colorWithWhite:1.0 alpha:0.4];
    [cardStack addArrangedSubview:descHeader];

    self.descLabel = [[UILabel alloc] init];
    self.descLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightRegular];
    self.descLabel.textColor = [UIColor colorWithWhite:1.0 alpha:0.8];
    self.descLabel.numberOfLines = 0;
    [cardStack addArrangedSubview:self.descLabel];

    UIView *sep2 = [[UIView alloc] init];
    sep2.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.1];
    sep2.translatesAutoresizingMaskIntoConstraints = NO;
    [sep2.heightAnchor constraintEqualToConstant:1].active = YES;
    [cardStack addArrangedSubview:sep2];

    self.dueDateLabel = [self makeInfoRow:@"calendar" label:@"Due Date"];
    self.dueDateLabel.textColor = [UIColor colorWithWhite:1.0 alpha:0.6];
    [cardStack addArrangedSubview:self.dueDateLabel];

    self.createdDateLabel = [self makeInfoRow:@"clock" label:@"Created"];
    self.createdDateLabel.textColor = [UIColor colorWithWhite:1.0 alpha:0.6];
    [cardStack addArrangedSubview:self.createdDateLabel];

    self.actionStack = [[UIStackView alloc] init];
    self.actionStack.axis = UILayoutConstraintAxisVertical;
    self.actionStack.spacing = 12;
    [content addArrangedSubview:self.actionStack];

    UIButton *deleteBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    [deleteBtn setTitle:@"Delete Task" forState:UIControlStateNormal];
    [deleteBtn setTitleColor:[UIColor systemRedColor] forState:UIControlStateNormal];
    deleteBtn.titleLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightSemibold];
    deleteBtn.backgroundColor = [UIColor colorWithRed:1.0 green:0.2 blue:0.2 alpha:0.1];
    deleteBtn.layer.cornerRadius = 14;
    deleteBtn.clipsToBounds = YES;
    [deleteBtn.heightAnchor constraintEqualToConstant:52].active = YES;
    [deleteBtn addTarget:self action:@selector(deleteTapped) forControlEvents:UIControlEventTouchUpInside];
    [content addArrangedSubview:deleteBtn];
}

#pragma mark - Populate Data

- (void)populateData {
    if (!self.task) return;

    self.titleLabel.text = self.task.title;
    self.descLabel.text = self.task.taskDescription.length > 0 ? self.task.taskDescription : @"No description";

    // Priority badge
    switch (self.task.priority) {
        case TaskPriorityLow:
            self.priorityBadge.text = @"  Low  ";
            self.priorityBadge.backgroundColor = [UIColor colorWithRed:0.18 green:0.55 blue:0.24 alpha:0.2];
            self.priorityBadge.textColor = [UIColor colorWithRed:0.3 green:0.8 blue:0.4 alpha:1.0];
            break;
        case TaskPriorityMedium:
            self.priorityBadge.text = @"  Medium  ";
            self.priorityBadge.backgroundColor = [UIColor colorWithRed:0.72 green:0.53 blue:0.04 alpha:0.2];
            self.priorityBadge.textColor = [UIColor colorWithRed:1.0 green:0.8 blue:0.2 alpha:1.0];
            break;
        case TaskPriorityHigh:
            self.priorityBadge.text = @"  High  ";
            self.priorityBadge.backgroundColor = [UIColor colorWithRed:0.80 green:0.18 blue:0.18 alpha:0.2];
            self.priorityBadge.textColor = [UIColor colorWithRed:1.0 green:0.4 blue:0.4 alpha:1.0];
            break;
    }

    // Status badge
    switch (self.task.status) {
        case TaskStatusTodo:
            self.statusBadge.text = @"  To Do  ";
            self.statusBadge.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.1];
            self.statusBadge.textColor = [UIColor whiteColor];
            break;
        case TaskStatusInProgress:
            self.statusBadge.text = @"  In Progress  ";
            self.statusBadge.backgroundColor = [UIColor colorWithRed:0.95 green:0.60 blue:0.07 alpha:0.2];
            self.statusBadge.textColor = [UIColor orangeColor];
            break;
        case TaskStatusDone:
            self.statusBadge.text = @"  Done  ";
            self.statusBadge.backgroundColor = [UIColor colorWithRed:0.20 green:0.72 blue:0.35 alpha:0.2];
            self.statusBadge.textColor = [UIColor greenColor];
            break;
    }

    NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
    fmt.dateStyle = NSDateFormatterMediumStyle;
    fmt.timeStyle = NSDateFormatterShortStyle;
    self.dueDateLabel.text = [NSString stringWithFormat:@"Due: %@", self.task.dueDate ? [fmt stringFromDate:self.task.dueDate] : @"Not set"];
    self.createdDateLabel.text = [NSString stringWithFormat:@"Created: %@", self.task.createdAt ? [fmt stringFromDate:self.task.createdAt] : @"Unknown"];

    for (UIView *v in self.actionStack.arrangedSubviews) {
        [v removeFromSuperview];
    }

    if (self.task.status != TaskStatusTodo) {
        [self.actionStack addArrangedSubview:[self makeActionButton:@"Move to To Do"
                                                               icon:@"arrow.uturn.backward.circle.fill"
                                                              color:ThemeAccentColor()
                                                             action:@selector(moveToTodo)]];
    }
    if (self.task.status != TaskStatusInProgress) {
        [self.actionStack addArrangedSubview:[self makeActionButton:@"Move to In Progress"
                                                               icon:@"hourglass.circle.fill"
                                                              color:[UIColor colorWithRed:0.95 green:0.60 blue:0.07 alpha:1.0]
                                                             action:@selector(moveToInProgress)]];
    }
    if (self.task.status != TaskStatusDone) {
        [self.actionStack addArrangedSubview:[self makeActionButton:@"Mark as Done"
                                                               icon:@"checkmark.circle.fill"
                                                              color:[UIColor colorWithRed:0.20 green:0.72 blue:0.35 alpha:1.0]
                                                             action:@selector(moveToDone)]];
    }
}

#pragma mark - Actions

- (void)moveToTodo {
    self.task.status = TaskStatusTodo;
    [[TaskManager sharedManager] updateTask:self.task];
}

- (void)moveToInProgress {
    self.task.status = TaskStatusInProgress;
    [[TaskManager sharedManager] updateTask:self.task];
}

- (void)moveToDone {
    self.task.status = TaskStatusDone;
    [[TaskManager sharedManager] updateTask:self.task];
}

- (void)editTapped {
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    UINavigationController *nav = [sb instantiateViewControllerWithIdentifier:@"NavAddVC"];
    AddViewController *addVC = (AddViewController *)nav.topViewController;
    addVC.taskToEdit = self.task;
    [self presentViewController:nav animated:YES completion:nil];
}

- (void)deleteTapped {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Delete Task"
                                                                    message:@"Are you sure you want to delete this task?"
                                                             preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil]];
    [alert addAction:[UIAlertAction actionWithTitle:@"Delete" style:UIAlertActionStyleDestructive handler:^(UIAlertAction *a) {
        [[TaskManager sharedManager] deleteTask:self.task];
        [self.navigationController popViewControllerAnimated:YES];
    }]];
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)onTaskUpdated {
    [self populateData];
}

#pragma mark - Helpers

- (UIView *)makeCard {
    UIView *card = [[UIView alloc] init];
    card.backgroundColor = [UIColor colorWithWhite:1.0 alpha:0.05];
    card.layer.cornerRadius = 24;
    return card;
}

- (UILabel *)makeBadge {
    UILabel *badge = [[UILabel alloc] init];
    badge.font = [UIFont systemFontOfSize:13 weight:UIFontWeightSemibold];
    badge.layer.cornerRadius = 8;
    badge.clipsToBounds = YES;
    badge.textAlignment = NSTextAlignmentCenter;
    return badge;
}

- (UILabel *)makeInfoRow:(NSString *)iconName label:(NSString *)text {
    UILabel *lbl = [[UILabel alloc] init];
    lbl.font = [UIFont systemFontOfSize:15 weight:UIFontWeightMedium];
    lbl.text = text;
    lbl.numberOfLines = 0;
    return lbl;
}

- (UIButton *)makeActionButton:(NSString *)title icon:(NSString *)iconName color:(UIColor *)color action:(SEL)action {
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeSystem];
    UIImageSymbolConfiguration *cfg = [UIImageSymbolConfiguration configurationWithPointSize:18 weight:UIImageSymbolWeightMedium];
    UIImage *img = [UIImage systemImageNamed:iconName withConfiguration:cfg];

    [btn setImage:img forState:UIControlStateNormal];
    [btn setTitle:[NSString stringWithFormat:@"  %@", title] forState:UIControlStateNormal];
    btn.tintColor = [UIColor whiteColor];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    btn.titleLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightSemibold];
    btn.backgroundColor = color;
    btn.layer.cornerRadius = 16;
    btn.clipsToBounds = YES;
    [btn.heightAnchor constraintEqualToConstant:56].active = YES;
    [btn addTarget:self action:action forControlEvents:UIControlEventTouchUpInside];
    return btn;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
