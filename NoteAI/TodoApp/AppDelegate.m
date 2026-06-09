// AppDelegate.m
#import "AppDelegate.h"
#import <UserNotifications/UserNotifications.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Request Notification Permissions
    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
    [center requestAuthorizationWithOptions:(UNAuthorizationOptionAlert | UNAuthorizationOptionSound | UNAuthorizationOptionBadge)
                          completionHandler:^(BOOL granted, NSError * _Nullable error) {
        if (!granted) {
            NSLog(@"Notification permission denied.");
        }
    }];

    // ── Global Theme ──
    UIColor *accent = [UIColor colorWithRed:0.424 green:0.388 blue:1.0 alpha:1.0]; // #6C63FF
    UIColor *darkBg = [UIColor colorWithRed:0.12 green:0.12 blue:0.13 alpha:1.0];

    // Navigation Bar Appearance
    UINavigationBarAppearance *navAppearance = [[UINavigationBarAppearance alloc] init];
    [navAppearance configureWithTransparentBackground];
    navAppearance.backgroundColor = [UIColor clearColor];
    navAppearance.titleTextAttributes = @{
        NSForegroundColorAttributeName: [UIColor whiteColor],
        NSFontAttributeName: [UIFont systemFontOfSize:18 weight:UIFontWeightBold]
    };

    [UINavigationBar appearance].standardAppearance = navAppearance;
    [UINavigationBar appearance].scrollEdgeAppearance = navAppearance;
    [UINavigationBar appearance].compactAppearance = navAppearance;
    [UINavigationBar appearance].tintColor = [UIColor whiteColor];

    // Search bar
    [[UITextField appearanceWhenContainedInInstancesOfClasses:@[[UISearchBar class]]] setTextColor:[UIColor whiteColor]];
    [[UISearchBar appearance] setTintColor:[UIColor whiteColor]];

    // Tab Bar Appearance
    UITabBarAppearance *tabAppearance = [[UITabBarAppearance alloc] init];
    [tabAppearance configureWithOpaqueBackground];
    tabAppearance.backgroundColor = [UIColor colorWithRed:0.08 green:0.08 blue:0.09 alpha:1.0]; // Near black

    // Selected icon/title = accent color
    UITabBarItemAppearance *itemAppearance = tabAppearance.stackedLayoutAppearance;
    itemAppearance.selected.iconColor = accent;
    itemAppearance.selected.titleTextAttributes = @{ NSForegroundColorAttributeName: accent };
    itemAppearance.normal.iconColor = [UIColor colorWithWhite:1.0 alpha:0.4];
    itemAppearance.normal.titleTextAttributes = @{ NSForegroundColorAttributeName: [UIColor colorWithWhite:1.0 alpha:0.4] };

    [UITabBar appearance].standardAppearance = tabAppearance;
    if (@available(iOS 15.0, *)) {
        [UITabBar appearance].scrollEdgeAppearance = tabAppearance;
    }
    [UITabBar appearance].tintColor = accent;

    return YES;
}

@end
