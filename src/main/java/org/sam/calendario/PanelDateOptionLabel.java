package org.sam.calendario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PanelDateOptionLabel {
    private final List<Item> listItems = new ArrayList<>();

    public PanelDateOptionLabel() {}

    public void add(String label, LabelCallback callback) {
        listItems.add(new Item(label, callback));
    }

    public List<Item> getListItems() {
        return listItems;
    }

    public record Item(String label, LabelCallback callback) {
    }

    public interface LabelCallback {
        LabelCallback TODAY = () -> new LocalDate[]{LocalDate.now()};
        LabelCallback YESTERDAY = () -> new LocalDate[]{LocalDate.now().minusDays(1)};
        LabelCallback LAST_7_DAYS = () -> new LocalDate[]{LocalDate.now().minusDays(7), LocalDate.now()};
        LabelCallback LAST_30_DAYS = () -> new LocalDate[]{LocalDate.now().minusDays(30), LocalDate.now()};
        LabelCallback THIS_MONTH = () -> {
            LocalDate now = LocalDate.now();
            return new LocalDate[]{now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth())};
        };
        LabelCallback LAST_MONTH = () -> {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            return new LocalDate[]{lastMonth.withDayOfMonth(1), lastMonth.withDayOfMonth(lastMonth.lengthOfMonth())};
        };
        LabelCallback LAST_YEAR = () -> {
            LocalDate lastYear = LocalDate.now().minusYears(1).withDayOfYear(1);
            return new LocalDate[]{lastYear, lastYear.withDayOfYear(lastYear.lengthOfYear())};
        };

        LabelCallback CUSTOM = null;

        LocalDate[] getDate();
    }
}
