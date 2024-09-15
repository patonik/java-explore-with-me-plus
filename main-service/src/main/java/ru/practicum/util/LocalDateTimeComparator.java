package ru.practicum.util;

import java.util.Comparator;

public class LocalDateTimeComparator implements Comparator<Statistical> {
    @Override
    public int compare(Statistical x, Statistical y) {
        return x.getCreatedOn().isBefore(y.getCreatedOn()) ? -1 :
            x.getCreatedOn().isAfter(y.getCreatedOn()) ? 1 : 0;
    }
}
