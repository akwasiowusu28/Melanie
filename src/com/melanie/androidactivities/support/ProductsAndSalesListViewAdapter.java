package com.melanie.androidactivities.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;

import java.util.List;

/**
 * The is the adapter used for any listview whose contents are of the form,
 * Name, qty, price, total.
 *
 * @param <T> In this case, T is either product or sale
 * @author Akwasi Owusu
 */
public class ProductsAndSalesListViewAdapter<T> extends ArrayAdapter<T> {

    private static final int SECTION = 0;
    private static final int ITEM = 1;
    private Context context;
    private int mainResource;
    private List<T> items;
    private boolean hasSections;
    private int sectionResource;
    private View longClickedView;
    private OnLongClickListener longClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            longClickedView = v;
            return false;
        }
    };

    public ProductsAndSalesListViewAdapter(Context context, List<T> items,
                                           boolean hasSections) {
        super(context, R.layout.layout_sales_list, items);
        this.context = context;
        this.items = items;
        this.hasSections = hasSections;
        this.mainResource = R.layout.layout_sales_list;
        this.sectionResource = R.layout.layout_section_header;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int viewType = getItemViewType(position);

        T item = items.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(
                    viewType == SECTION ? sectionResource : mainResource,
                    parent, false);
        }

        int quantity = 0;
        double price = 0;
        String name = "";

        if (item != null)
            if (item instanceof Sale) {

                Sale sale = (Sale) item;
                quantity = sale.getQuantitySold();
                price = sale.getProduct().getPrice();
                name = sale.getProduct().getProductName();
                setValues(convertView, name, quantity, price);
            } else if (item instanceof Product) {

                Product product = (Product) item;
                quantity = product.getQuantity();
                price = product.getPrice();
                name = product.getProductName();
                setValues(convertView, name, quantity, price);
            } else if (item instanceof SectionHeader) {
                setSectionValue(convertView,
                        ((SectionHeader) item).getSectionText());
            }
        convertView.setOnLongClickListener(longClickListener);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {

        return hasSections && items.get(position) instanceof SectionHeader ? SECTION
                : ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return hasSections ? 2 : 1;
    }

    public View getLongClickedView() {
        return longClickedView;
    }

    private void setValues(View rowView, String name, int quantity, double price) {

        ((TextView) rowView.findViewById(R.id.productNameTextView))
                .setText(name);
        ((TextView) rowView.findViewById(R.id.qtyTextView)).setText(String
                .valueOf(quantity));
        ((TextView) rowView.findViewById(R.id.unitPriceTextView))
                .setText(String.valueOf(price));
        ((TextView) rowView.findViewById(R.id.totalTextView)).setText(String
                .valueOf(quantity * price));
    }

    private void setSectionValue(View rowView, String sectionValue) {

        ((TextView) rowView.findViewById(R.id.sectionTextView))
                .setText(sectionValue);
    }
}
