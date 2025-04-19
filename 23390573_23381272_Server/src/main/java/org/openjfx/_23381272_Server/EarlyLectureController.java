
package org.openjfx._23381272_Server;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class EarlyLectureController {

    private static final List<String> DAY_ORDER = Arrays.asList(
        "Monday","Tuesday","Wednesday","Thursday","Friday");
    
    private static final int THRESHOLD_DAYS = 1;  // 1 day per task

    private class TimeTableAdjustment extends RecursiveTask<Map<String,String>> {
        private final List<String> days;  // e.g. ["Monday","Tuesday",...]
        private final int left, right;
        private final Map<String,String> original;

        TimeTableAdjustment(List<String> days, int left, int right, Map<String,String> original) {
            this.days          = days;
            this.left          = left;
            this.right         = right;
            this.original      = original;
        }

        @Override
        protected Map<String,String> compute() {
            if (right - left + 1 <= THRESHOLD_DAYS) {
                String day = days.get(left);
                // 1) Collect that day’s entries
                List<Map.Entry<String,String>> list = new ArrayList<>();
                Pattern splitPat = Pattern.compile("@");
                for (var e : original.entrySet()) {
                    String[] parts = splitPat.split(e.getValue());
                    if (parts[4].equalsIgnoreCase(day)) {
                        list.add(e);
                    }
                }
                list.sort(Comparator.comparing(e -> e.getValue()
                    .split(Pattern.quote("@"))[5]));

                Map<String,String> result = new HashMap<>();
                int currentHour = 9;
                
                for (int i = 0; i < list.size(); i++) {
                    var e = list.get(i);
                    String[] parts = splitPat.split(e.getValue());
                    String time = parts[5];
                    parts[5] = String.format("%02d%s", currentHour, time.substring(2));
                    
                    String newDetails = String.join("@", parts);
                    String newKey = parts[0] + "_" + parts[3] + "_" + parts[2] + "_" + parts[4] + "_" + parts[5];
                    
                    result.put(newKey, newDetails);
                    
                    int duration = Integer.parseInt(parts[6]);
                    currentHour = Math.min(currentHour + duration, 23);
                }
                return result;
            }

            int mid = (left + right) >>> 1;
            TimeTableAdjustment leftTask  = new TimeTableAdjustment(days, left, mid, original);
            TimeTableAdjustment rightTask = new TimeTableAdjustment(days, mid+1, right,original);

            leftTask.fork();
            Map<String,String> rightMap = rightTask.compute();
            Map<String,String> leftMap  = leftTask.join();
            leftMap.putAll(rightMap);
            return leftMap;
        }
    }

    public Map<String,String> adjustTimetableParallel(Map<String,String> map) {
        
        ForkJoinPool pool = ForkJoinPool.commonPool();
        return pool.invoke(new TimeTableAdjustment(
            DAY_ORDER, 0, DAY_ORDER.size()-1, map));
    }
}
