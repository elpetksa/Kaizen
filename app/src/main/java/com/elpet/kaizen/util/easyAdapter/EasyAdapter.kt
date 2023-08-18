package com.elpet.kaizen.util.easyAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView

class EasyAdapter<T, VH: EasyAdapter.EasyAdapterViewHolder<T>>(
    @LayoutRes private val layout: Int,
    private val initializer: (View) -> VH
) : RecyclerView.Adapter<EasyAdapter.EasyAdapterViewHolder<T>>() {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ VIEW HOLDER INTERFACE                                                                     ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝

    /**
     * Interface used to view holder for [EasyAdapter].
     */
    abstract class EasyAdapterViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
        /**
         * Called when the adapter needs to bind this view holder with a data object. Initialize
         * your view here.
         *
         * @param data     Corresponding data to bind with this view.
         * @param position The position of this view holder. Used to identify if last etc.
         * @param total    Defines how many items adapter currently has.
         */
        abstract fun bindViewHolder(data: T, position: Int, total: Int)

        /**
         * Corresponding holder data.
         */
        var data: T? = null
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ VARIABLES                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PRIVATE VARIABLES
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * List containing data to show.
     */
    private var dataList: MutableList<T> = mutableListOf()

    var viewHolders: MutableList<VH> = mutableListOf()

    /**
     * Lock to perform synchronized operations on data.
     */
    private val lock = Any()

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ FUNCTIONS                                                                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → OVERRIDE FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Called when RecyclerView needs a new [RecyclerView.ViewHolder] of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(BillingPlanHolder, int)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EasyAdapterViewHolder<T> {
        return initializer(
            LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        )
    }

    override fun onViewAttachedToWindow(holder: EasyAdapterViewHolder<T>) {
        viewHolders.add(holder as VH)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: EasyAdapterViewHolder<T>) {
        viewHolders.remove(holder)
        super.onViewDetachedFromWindow(holder)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the ViewHolder.itemView to reflect the item at the given
     * position.
     *
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: EasyAdapterViewHolder<T>, position: Int) {
        val data = dataList[position]
        holder.data = data
        holder.bindViewHolder(data, position, itemCount)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return dataList.size
    }

    // ┌───────────────────────────────────────────────────────────────────────────────────────────┐
    //   → PUBLIC FUNCTIONS
    // └───────────────────────────────────────────────────────────────────────────────────────────┘

    /**
     * Returns the data of the adapter. Make sure to set data first by calling [setData].
     *
     * @return List of [T] data.
     */
    fun getData(): List<T> {
        return dataList
    }

    /**
     * Replaces current adapter data with given one.
     *
     * Notify that adapter will automatically update it's layout. You do not need to perform any
     * update operations.
     *
     * If you are using this function to sort adapter's data list, consider using [sort] instead.
     *
     * @param data List of numbers to show in the list.
     */
    @SuppressLint("NotifyDataSetChanged")
    @UiThread
    fun setData(data: List<T>) {
        synchronized(lock) {
            // Set data.
            dataList = data.toMutableList()

            // Update adapter.
            notifyDataSetChanged()
        }
    }

    /**
     * Sorts current adapter data list using given [comparator]. It is better to call this function
     * for sorting instead of sorting the list on your own and calling [setData] since this calls
     * [notifyItemRangeChanged] instead.
     *
     * @param comparator Comparator used to sort current adapter data list.
     */
    fun sort(comparator: Comparator<in T>) {
        synchronized(lock) {
            // Set new data.
            dataList.sortWith(comparator)

            // Update adapter.
            notifyItemRangeChanged(0, dataList.size)
        }
    }

    /**
     * Adds given data value to the data list of this adapter. You do not need to perform any update
     * operations. Adapter will be notified from here.
     *
     * @param data Data object to add to adapter's data list.
     *
     * @see addData
     */
    fun addData(data: T) {
        synchronized(lock) {
            // Add number to list.
            dataList.add(data)

            // Update adapter.
            notifyItemRangeInserted(dataList.size - 1, 1)
        }
    }

    /**
     * Adds given data values to the data list of this adapter. You do not need to perform any
     * update operations. Adapter will be notified from here.
     *
     * @param data List of data object to add to adapter's data list.
     *
     * @see addData
     */
    fun addData(data: List<T>) {
        synchronized(lock) {
            // Keep a value of the current list size.
            val lastSize = dataList.size

            // Add number to list.
            dataList.addAll(data)

            // Update adapter.
            notifyItemRangeInserted(lastSize - 1, data.size)
        }
    }

    fun clear() {
        synchronized(lock) {
            // Keep a value of the current list size.
            val lastSize = dataList.size

            // Remove all data from list.
            dataList.clear()

            // Update adapter.
            notifyItemRangeChanged(0, lastSize - 1)
        }
    }

    /**
     * Removes given data from the adapter's data list. You do not need to perform and update
     * operations. Adapter will be notified from here.
     *
     * @param data Data object to remove from adapter's list.
     */
    fun removeData(data: T) {
        // Check if list contains item.
        dataList.indexOf(data).let {
            // Remove data from list.
            dataList.remove(data)

            // Update adapter.
            notifyItemRemoved(it)
        }
    }

    /**
     * Updates data at adapter data list. If given data is not present in the list then it will be
     * added. You do not need to perform any update operations. Adapter is notified from here.
     *
     * Notice that in order to identify if given [data] object is in the list, you really need to
     * override `equals` operator function unless [T] object is primitive (int, string etc.).
     *
     * @param data Data to replace it's old value in the adapter.
     */
    fun updateData(data: T) {
        // Check if list contains item.
        synchronized(lock) {
            dataList.indexOf(data).let {
                when (it == -1) {
                    true -> {
                        // List does not contain data. Add now.
                        addData(data)
                    }
                    false -> {
                        // Found data. Replace it.
                        dataList[it] = data
                        notifyItemChanged(it)
                    }
                }
            }
        }
    }

}