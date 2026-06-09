// TaskCell.m
#import "TaskCell.h"

@implementation TaskCell

- (void)awakeFromNib {
    [super awakeFromNib];
    self.backgroundColor = [UIColor clearColor];
    self.contentView.backgroundColor = [UIColor clearColor];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    
    // Modern Rounded Shape
    self.cardView.layer.cornerRadius = 24.0f;
    self.cardView.clipsToBounds = YES; // Clean cut
    
    // Flat look — remove shadow for simplicity
    self.cardView.layer.shadowOpacity = 0.0;
    
    // Description label: allow multi-line wrapping
    self.detailLabel.numberOfLines = 3;
    self.detailLabel.lineBreakMode = NSLineBreakByWordWrapping;
    
    // Title: bold large
    self.titleLabel.font = [UIFont systemFontOfSize:22.0f weight:UIFontWeightBold];
    self.titleLabel.textColor = [UIColor colorWithRed:0.1 green:0.1 blue:0.12 alpha:1.0];
    
    // Description: Medium weight, slightly muted dark grey
    self.detailLabel.font = [UIFont systemFontOfSize:15.0f weight:UIFontWeightMedium];
    self.detailLabel.textColor = [UIColor colorWithRed:0.15 green:0.15 blue:0.18 alpha:0.8];
    
    // Date: Small, muted dark grey
    self.dateLabel.font = [UIFont systemFontOfSize:13.0f weight:UIFontWeightSemibold];
    self.dateLabel.textColor = [UIColor colorWithRed:0.2 green:0.2 blue:0.25 alpha:0.6];
    
    // Status label — hide or show as a subtle accent
    self.statusLabel.hidden = YES;
}

- (void)configureWithTask:(Task *)task {
    self.titleLabel.text = task.title;
    self.detailLabel.text = task.taskDescription;
    
    // Format dueDate
    if (task.dueDate) {
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"MMM d, yyyy"];
        self.dateLabel.text = [formatter stringFromDate:task.dueDate];
    } else {
        self.dateLabel.text = @"No due date";
    }
    
    // Vibrant Color Palette
    NSArray<UIColor *> *vibrantColors = @[
        [UIColor colorWithRed:1.00 green:0.50 blue:0.96 alpha:1.0], // #FF80F4 (Pink)
        [UIColor colorWithRed:1.00 green:0.61 blue:0.61 alpha:1.0], // #FF9B9B (Salmon)
        [UIColor colorWithRed:1.00 green:0.95 blue:0.61 alpha:1.0], // #FFF29B (Yellow)
        [UIColor colorWithRed:0.65 green:0.61 blue:1.00 alpha:1.0]  // #A79BFF (Purple)
    ];
    
    // Cycle through colors based on character sum of the title for semi-consistent color mapping
    NSInteger colorIndex = 0;
    if (task.title.length > 0) {
        colorIndex = [task.title characterAtIndex:0] % vibrantColors.count;
    } else {
        colorIndex = (NSInteger)task.priority % vibrantColors.count;
    }
    
    self.cardView.backgroundColor = vibrantColors[colorIndex];
}

- (void)prepareForReuse {
    [super prepareForReuse];
    self.titleLabel.text = nil;
    self.detailLabel.text = nil;
    self.dateLabel.text = nil;
    self.statusLabel.text = nil;
}

@end
