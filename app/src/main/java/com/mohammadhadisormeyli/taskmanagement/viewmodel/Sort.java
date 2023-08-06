package com.mohammadhadisormeyli.taskmanagement.viewmodel;

public class Sort {

    private int order = 0;
    private boolean reverse = false;

    public Sort() {
    }

    public Sort(int order, boolean reverse) {
        this.order = order;
        this.reverse = reverse;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
