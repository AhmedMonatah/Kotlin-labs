// DoneViewController.m
#import "DoneViewController.h"
#import "TaskCell.h"
#import "TaskManager.h"
#import "DetailViewController.h"

@interface DoneViewController ()
@property (nonatomic, strong) NSArray<Task *> *tasks;
@property (nonatomic, strong) NSArray<Task *> *filteredTasks;
@property (nonatomic, strong) UISearchBar *searchBar;
@property (nonatomic, assign) NSInteger selectedPriorityIndex; // 0:All 1:Low 2:Med 3:High
@property (nonatomic, assign) NSInteger selectedDateFilterIndex; // 0:All 1:Week 2:Month
@property (nonatomic, strong) UIView *headerContainer;
@end

@implementation DoneViewController

static UIColor *DarkBg(void)     { return [UIColor colorWithRed:0.10 green:0.10 blue:0.12 alpha:1.0]; }
static UIColor *CardBg(void)     { return [UIColor colorWithRed:0.16 green:0.16 blue:0.20 alpha:1.0]; }
static UIColor *AccentPurple(void){ return [UIColor colorWithRed:0.424 green:0.388 blue:1.0 alpha:1.0]; }
static UIColor *AccentGreen(void) { return [UIColor colorWithRed:0.20 green:0.78 blue:0.35 alpha:1.0]; }

- (void)viewDidLoad {
    [super viewDidLoad];
    [self applyTheme];
    [self setupHeader];
    [self loadData];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(loadData)
                                                 name:TaskManagerDidUpdateNotification
                                               object:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:animated];
    [self loadData];
}

#pragma mark - Theme

- (void)applyTheme {
    self.view.backgroundColor = DarkBg();
    self.tableView.backgroundColor = DarkBg();
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.contentInset = UIEdgeInsetsMake(0, 0, 30, 0);
}

#pragma mark - Custom Header
// Header: title (70) + search (50) + priority filter (50) + date filter (50) = 220

- (void)setupHeader {
    self.headerContainer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 222)];
    self.headerContainer.backgroundColor = [UIColor clearColor];

    // --- Title ---
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 24, 260, 34)];
    titleLabel.text = @"History";
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.font = [UIFont systemFontOfSize:28 weight:UIFontWeightBold];
    [self.headerContainer addSubview:titleLabel];

    // Subtitle badge "Done"
    UILabel *doneBadge = [[UILabel alloc] initWithFrame:CGRectMake(132, 30, 60, 22)];
    doneBadge.text = @"Done";
    doneBadge.textColor = AccentGreen();
    doneBadge.font = [UIFont systemFontOfSize:14 weight:UIFontWeightSemibold];
    doneBadge.backgroundColor = [AccentGreen() colorWithAlphaComponent:0.15];
    doneBadge.layer.cornerRadius = 8;
    doneBadge.clipsToBounds = YES;
    doneBadge.textAlignment = NSTextAlignmentCenter;
    [self.headerContainer addSubview:doneBadge];

    // --- Search Bar ---
    self.searchBar = [[UISearchBar alloc] initWithFrame:CGRectMake(12, 68, self.view.frame.size.width - 24, 44)];
    self.searchBar.placeholder = @"Search history...";
    self.searchBar.delegate = self;
    self.searchBar.searchBarStyle = UISearchBarStyleMinimal;
    self.searchBar.tintColor = AccentPurple();

    UITextField *tf = self.searchBar.searchTextField;
    tf.backgroundColor = CardBg();
    tf.textColor = [UIColor whiteColor];
    tf.tintColor = AccentPurple();
    tf.font = [UIFont systemFontOfSize:15 weight:UIFontWeightMedium];
    tf.layer.cornerRadius = 14;
    tf.clipsToBounds = YES;
    tf.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"Search history..."
        attributes:@{NSForegroundColorAttributeName: [UIColor colorWithWhite:1.0 alpha:0.4]}];
    UIImageView *searchIcon = (UIImageView *)tf.leftView;
    searchIcon.tintColor = [UIColor colorWithWhite:1.0 alpha:0.4];

    [self.headerContainer addSubview:self.searchBar];

    // --- Priority Filter ---
    UISegmentedControl *priorityFilter = [[UISegmentedControl alloc] initWithItems:@[@"All", @"Low", @"Medium", @"High"]];
    priorityFilter.frame = CGRectMake(12, 122, self.view.frame.size.width - 24, 34);
    priorityFilter.selectedSegmentIndex = 0;
    priorityFilter.tag = 200;
    [priorityFilter addTarget:self action:@selector(priorityFilterChanged:) forControlEvents:UIControlEventValueChanged];
    priorityFilter.backgroundColor = CardBg();
    [priorityFilter setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor],
                                             NSFontAttributeName: [UIFont systemFontOfSize:13 weight:UIFontWeightSemibold]}
                                  forState:UIControlStateSelected];
    [priorityFilter setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithWhite:1.0 alpha:0.5],
                                             NSFontAttributeName: [UIFont systemFontOfSize:13 weight:UIFontWeightMedium]}
                                  forState:UIControlStateNormal];
    priorityFilter.selectedSegmentTintColor = AccentPurple();
    [self.headerContainer addSubview:priorityFilter];

    // --- Date Filter ---
    UISegmentedControl *dateFilter = [[UISegmentedControl alloc] initWithItems:@[@"All Time", @"This Week", @"This Month"]];
    dateFilter.frame = CGRectMake(12, 168, self.view.frame.size.width - 24, 34);
    dateFilter.selectedSegmentIndex = 0;
    dateFilter.tag = 201;
    [dateFilter addTarget:self action:@selector(dateFilterChanged:) forControlEvents:UIControlEventValueChanged];
    dateFilter.backgroundColor = CardBg();
    [dateFilter setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor],
                                         NSFontAttributeName: [UIFont systemFontOfSize:13 weight:UIFontWeightSemibold]}
                              forState:UIControlStateSelected];
    [dateFilter setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithWhite:1.0 alpha:0.5],
                                         NSFontAttributeName: [UIFont systemFontOfSize:13 weight:UIFontWeightMedium]}
                              forState:UIControlStateNormal];
    dateFilter.selectedSegmentTintColor = AccentGreen();
    [self.headerContainer addSubview:dateFilter];

    self.tableView.tableHeaderView = self.headerContainer;
}

- (void)priorityFilterChanged:(UISegmentedControl *)sender {
    self.selectedPriorityIndex = sender.selectedSegmentIndex;
    [self applyFilters];
}

- (void)dateFilterChanged:(UISegmentedControl *)sender {
    self.selectedDateFilterIndex = sender.selectedSegmentIndex;
    [self applyFilters];
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

    // Text search
    if (searchText.length > 0) {
        [predicates addObject:[NSPredicate predicateWithFormat:
            @"title CONTAINS[cd] %@ OR taskDescription CONTAINS[cd] %@", searchText, searchText]];
    }

    // Priority filter
    if (self.selectedPriorityIndex > 0) {
        TaskPriority priority = (TaskPriority)(self.selectedPriorityIndex - 1);
        [predicates addObject:[NSPredicate predicateWithFormat:@"priority == %d", (int)priority]];
    }

    // Date filter (based on createdAt or dueDate — using dueDate here)
    if (self.selectedDateFilterIndex > 0) {
        NSCalendar *calendar = [NSCalendar currentCalendar];
        NSDate *now = [NSDate date];
        NSDateComponents *components = [[NSDateComponents alloc] init];

        if (self.selectedDateFilterIndex == 1) {      // This Week
            components.day = -7;
        } else if (self.selectedDateFilterIndex == 2) { // This Month
            components.month = -1;
        }

        NSDate *startDate = [calendar dateByAddingComponents:components toDate:now options:0];
        // Filter tasks whose dueDate falls within the window
        [predicates addObject:[NSPredicate predicateWithFormat:@"dueDate >= %@", startDate]];
    }

    self.filteredTasks = predicates.count == 0
        ? self.tasks
        : [self.tasks filteredArrayUsingPredicate:[NSCompoundPredicate andPredicateWithSubpredicates:predicates]];

    [self.tableView reloadData];
}

#pragma mark - Data

- (void)loadData {
    self.tasks = [[TaskManager sharedManager] doneTasks];
    [self applyFilters];
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

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
