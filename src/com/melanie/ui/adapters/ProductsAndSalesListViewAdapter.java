package com.melanie.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.ui.R;
import com.melanie.ui.support.SectionHeader;
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

        int quantity;
        double price;
        String name;


        //This could be refactored to use the visitor pattern. That is a much cleaner solution

        if (item != null)
            if (item instanceof Sale) {

                Sale sale = (Sale) item;
                quantity = sale.getQuantitySold();
                Product product = sale.getProduct();
                if(product != null){
                    price = product.getPrice();
                    name = product.getProductName();
                    setValues(convertView, name, quantity, price);
                }

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

        TextView productNameTextView = (TextView) rowView.findViewById(R.id.productNameTextView);
        if (productNameTextView != null)
            productNameTextView.setText(name);

        TextView qtyTextView = (TextView) rowView.findViewById(R.id.qtyTextView);
        if (qtyTextView != null)
            qtyTextView.setText(String.valueOf(quantity));

        TextView unitPriceTextView = (TextView) rowView.findViewById(R.id.unitPriceTextView);
        if (unitPriceTextView != null)
            unitPriceTextView.setText(String.valueOf(price));

        TextView totalTextView = (TextView) rowView.findViewById(R.id.totalTextView);
        if (totalTextView != null)
            totalTextView.setText(String.valueOf(quantity * price));
    }

    private void setSectionValue(View rowView, String sectionValue) {

        TextView sectionView = (TextView) rowView.findViewById(R.id.sectionTextView);
        if (sectionView != null)
            sectionView.setText(sectionValue);
    }
}
