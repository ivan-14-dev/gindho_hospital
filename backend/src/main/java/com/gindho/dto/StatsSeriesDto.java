package com.gindho.dto;

import java.util.List;

public class StatsSeriesDto {

    private String metric;
    private List<StatsPointDto> points;

    public StatsSeriesDto() {}

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public List<StatsPointDto> getPoints() {
        return points;
    }

    public void setPoints(List<StatsPointDto> points) {
        this.points = points;
    }

    public static class StatsPointDto {
        private String x;
        private Double y;

        public StatsPointDto() {}

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }
    }
}
