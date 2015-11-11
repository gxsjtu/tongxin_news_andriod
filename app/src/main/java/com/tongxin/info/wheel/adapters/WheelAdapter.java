package com.tongxin.info.wheel.adapters;

/**
 * Created by cc on 2015/11/6.
 */
public interface WheelAdapter {

    public int getItemsCount();

    /**
     * Gets a wheel item by index.
     *
     * @param index the item index
     * @return the wheel item text or null
     */
    public String getItem(int index);

    /**
     * Gets maximum item length. It is used to determine the wheel width.
     * If -1 is returned there will be used the default wheel width.
     *
     * @return the maximum item length or -1
     */
    public int getMaximumLength();
}
