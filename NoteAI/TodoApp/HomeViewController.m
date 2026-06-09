// HomeViewController.m
#import "HomeViewController.h"
#import "TaskCell.h"
#import "TaskManager.h"
#import "AddViewController.h"
#import "DetailViewController.h"

@interface HomeViewController ()
@property (nonatomic, strong) NSArray<Task *> *tasks;
@property (nonatomic, strong) NSArray<Task *> *filteredTasks;
@property (nonatomic, strong) UISearchBar *searchBar;
@property (nonatomic, assign) NSInteger selectedPriorityIndex; // 0:All 1:Low 2:Mid 3:High
@property (nonatomic, strong) UIView *headerContainer;
@property (nonatomic, strong) UIButton *fabButton;
@end

@implementation HomeViewController

static UIColor *DarkBg(void) { return [UIColor colorWithRed:0.10 green:0.10 blue:0.12 alpha:1.0]; }
static UIColor *CardBg(void) { return [UIColor colorWithRed:0.16 green:0.16 blue:0.20 alpha:1.0]; }
static UIColor *AccentPurple(void) { return [UIColor colorWithRed:0.424 green:0.388 blue:1.0 alpha:1.0]; }

- (void)viewDidLoad {
    [super viewDidLoad];
    [self applyTheme];
    [self setupHeader];
    [self setupFAB];
    [self loadData];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(loadData)
                                                 name:TaskManagerDidUpdateNotification
                                               object:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:animated];
    [self loadData]; // Refresh when returning
}

#pragma mark - Theme

- (void)applyTheme {
    self.view.backgroundColor = DarkBg();
    self.tableView.backgroundColor = DarkBg();
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.contentInset = UIEdgeInsetsMake(0, 0, 100, 0); // Space for FAB
}

#pragma mark - Custom Header

- (void)setupHeader {
    // Header height: title row (70) + search bar (50) + priority filter (50) = 170
    self.headerContainer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 170)];
    self.headerContainer.backgroundColor = [UIColor clearColor];

    // --- Row 1: Title + Add button ---
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 24, 220, 34)];
    titleLabel.text = @"All Tasks";
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.font = [UIFont systemFontOfSize:28 weight:UIFontWeightBold];
    [self.headerContainer addSubview:titleLabel];

    // --- Row 2: Search Bar ---
    self.searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(12, 68, self.view.frame.size.width - 24, 44)];
    self.searchBar.placeholder = @"Search tasks...";
    self.searchBar.delegate = self;
    self.searchBar.searchBarStyle = UISearchBarStyleMinimal;
    self.searchBar.barTintColor = [UIColor clearColor];
    self.searchBar.tintColor = AccentPurple();
    self.searchBar.layer.cornerRadius = 14;
    self.searchBar.clipsToBounds = YES;

    // Style the inner text field
    UITextField *tf = self.searchBar.searchTextField;
    tf.backgroundColor = CardBg();
    tf.textColor = [UIColor whiteColor];
    tf.tintColor = AccentPurple();
    tf.font = [UIFont systemFontOfSize:15 weight:UIFontWeightMedium];
    tf.layer.cornerRadius = 14;
    tf.clipsToBounds = YES;
    // Placeholder color
    tf.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"Search tasks..."
        attributes:@{NSForegroundColorAttributeName: [UIColor colorWithWhite:1.0 alpha:0.4]}];
    // Search icon color
    UIImageView *searchIcon = (UIImageView *)tf.leftView;
    searchIcon.tintColor = [UIColor colorWithWhite:1.0 alpha:0.4];

    [self.headerContainer addSubview:self.searchBar];

    // --- Row 3: Priority Filter Segment ---
    UISegmentedControl *priorityFilter = [[UISegmentedControl alloc] initWithItems:@[@"All", @"Low", @"Medium", @"High"]];
    priorityFilter.frame = CGRectMake(12, 122, self.view.frame.size.width - 24, 34);
    priorityFilter.selectedSegmentIndex = 0;
    priorityFilter.tag = 100;
    [priorityFilter addTarget:self action:@selector(priorityFilterChanged:) forControlEvents:UIControlEventValueChanged];

    // Dark segment styling
    priorityFilter.backgroundColor = CardBg();
    [priorityFilter setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor],
                                             NSFontAttributeName: [UIFont systemFontOfSize:13 weight:UIFontWeightSemibold]}
                                  forState:UIControlStateSelected];
    [priorityFilter setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithWhite:1.0 alpha:0.5],
                                             NSFontAttributeName: [UIFont systemFontOfSize:13 weight:UIFontWeightMedium]}
                                  forState:UIControlStateNormal];
    priorityFilter.selectedSegmentTintColor = AccentPurple();

    [self.headerContainer addSubview:priorityFilter];

    self.tableView.tableHeaderView = self.headerContainer;
}

- (void)priorityFilterChanged:(UISegmentedControl *)sender {
    self.selectedPriorityIndex = sender.selectedSegmentIndex;
    [self applyFilters];
}

#pragma mark - FAB

- (void)setupFAB {
    CGFloat fabSize = 60.0;
    self.fabButton = [UIButton buttonWithType:UIButtonTypeCustom];
    self.fabButton.translatesAutoresizingMaskIntoConstraints = NO;
    self.fabButton.backgroundColor = AccentPurple();

    UIImageSymbolConfiguration *cfg = [UIImageSymbolConfiguration configurationWithPointSize:24 weight:UIImageSymbolWeightBold];
    UIImage *img = [UIImage systemImageNamed:@"plus" withConfiguration:cfg];
    [self.fabButton setImage:img forState:UIControlStateNormal];
    self.fabButton.tintColor = [UIColor whiteColor];
    self.fabButton.layer.cornerRadius = fabSize / 2.0;
    self.fabButton.layer.shadowColor = AccentPurple().CGColor;
    self.fabButton.layer.shadowOffset = CGSizeMake(0, 6);
    self.fabButton.layer.shadowRadius = 12;
    self.fabButton.layer.shadowOpacity = 0.5;

    [self.fabButton addTarget:self action:@selector(addClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.fabButton];
    [self.view bringSubviewToFront:self.fabButton];

    [NSLayoutConstraint activateConstraints:@[
        [self.fabButton.trailingAnchor constraintEqualToAnchor:self.view.trailingAnchor constant:-24],
        [self.fabButton.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-80],
        [self.fabButton.widthAnchor constraintEqualToConstant:fabSize],
        [self.fabButton.heightAnchor constraintEqualToConstant:fabSize],
    ]];
}

#pragma mark - UISearchBarDelegate

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    [self applyFilters];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    [searchBar resignFirstResponder];
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar {
    searchBar.text = @"";
    [searchBar resignFirstResponder];
    [self applyFilters];
}

#pragma mark - Filtering

- (void)applyFilters {
    NSString *searchText = self.searchBar.text;
    NSMutableArray *predicates = [NSMutableArray array];

    if (searchText.length > 0) {
        [predicates addObject:[NSPredicate predicateWithFormat:
            @"title CONTAINS[cd] %@ OR taskDescription CONTAINS[cd] %@", searchText, searchText]];
    }

    if (self.selectedPriorityIndex > 0) {
        TaskPriority priority = (TaskPriority)(self.selectedPriorityIndex - 1);
        [predicates addObject:[NSPredicate predicateWithFormat:@"priority == %d", (int)priority]];
    }

    self.filteredTasks = predicates.count == 0
        ? self.tasks
        : [self.tasks filteredArrayUsingPredicate:[NSCompoundPredicate andPredicateWithSubpredicates:predicates]];

    [self.tableView reloadData];
}

#pragma mark - Data

- (void)loadData {
    self.tasks = [[TaskManager sharedManager] allTasks];
    [self applyFilters];
}

#pragma mark - Actions

- (IBAction)addClicked:(id)sender {
    [self performSegueWithIdentifier:@"showAdd" sender:nil];
}

#pragma mark - UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.filteredTasks.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    TaskCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TaskCell" forIndexPath:indexPath];
    [cell configureWithTask:self.filteredTasks[indexPath.row]];
    return cell;
}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 175;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    DetailViewController *detailVC = [[DetailViewController alloc] init];
    detailVC.task = self.filteredTasks[indexPath.row];
    [self.navigationController pushViewController:detailVC animated:YES];
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [[TaskManager sharedManager] deleteTask:self.filteredTasks[indexPath.row]];
    }
}

#pragma mark - Navigation

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"showAdd"]) {
        UINavigationController *nav = segue.destinationViewController;
        AddViewController *addVC = (AddViewController *)nav.topViewController;
        if ([sender isKindOfClass:[Task class]]) {
            addVC.taskToEdit = (Task *)sender;
        }
    }
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
