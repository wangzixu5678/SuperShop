package mobi.vhly.supershop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import mobi.vhly.supershop.R;
import mobi.vhly.supershop.event.CartOperationEvent;
import mobi.vhly.supershop.model.CartItem;

/**
 * Created by vhly[FR].
 * <p>
 * Author: vhly[FR]
 * Email: vhly@163.com
 * Date: 2016/10/22
 */

public class CartListAdapter extends BaseAdapter {

    private Context mContext;

    private List<CartItem> mItems;

    private boolean isEditMode;

    public CartListAdapter(Context context, List<CartItem> items) {
        mContext = context;
        mItems = items;
    }

    public void switchEditMode(){
        isEditMode = !isEditMode;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (mItems != null) {
            ret = mItems.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getProductId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = null;
        if(convertView != null){
            ret = convertView;
        }else{
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ret = inflater.inflate(R.layout.item_cart, parent, false);
        }

        ViewHolder holder = (ViewHolder) ret.getTag();
        if (holder == null) {
            holder = new ViewHolder(ret);
            ret.setTag(holder);
        }

        // show data info

        CartItem cartItem = mItems.get(position);

        holder.setCartItem(cartItem);

        holder.mImageView.setImageResource(R.mipmap.ic_launcher);
        holder.mTextName.setText(cartItem.getProductName());
        holder.mTextPrice.setText(String.format("¥ %.2f", cartItem.getProductPrice()));
        int count = cartItem.getCount();
        holder.mTextCount.setText(Integer.toString(count));


        // 如果Adapter中，根据状态，一个控件显示不同状态，那么条件必须要判断全 true/false

        holder.mCheckBox.setChecked(cartItem.isChecked());

        if(count == 1){
            holder.mButtonDec.setEnabled(false);
        }else{
            holder.mButtonDec.setEnabled(true);
        }

        if(isEditMode){
            // Edit
            holder.mCheckBox.setVisibility(View.VISIBLE);
            holder.mButtonDel.setVisibility(View.VISIBLE);
        }else {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
            holder.mButtonDel.setVisibility(View.INVISIBLE);
        }

        return ret;
    }

    private static class ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

        private CheckBox mCheckBox;
        private ImageView mImageView;
        private TextView mTextName;
        private TextView mTextPrice;

        private Button mButtonDec; // -
        private Button mButtonInc; // +
        private TextView mTextCount; // count

        private Button mButtonDel;

        private CartItem mCartItem;

        ViewHolder(View itemView){
            mCheckBox = (CheckBox) itemView.findViewById(R.id.item_cart_check);
            mImageView = (ImageView) itemView.findViewById(R.id.item_cart_icon);
            mTextName = (TextView) itemView.findViewById(R.id.item_cart_name);
            mTextPrice = (TextView) itemView.findViewById(R.id.item_cart_price);
            mButtonDec = (Button) itemView.findViewById(R.id.item_cart_dec);
            mTextCount = (TextView) itemView.findViewById(R.id.item_cart_count);
            mButtonInc = (Button) itemView.findViewById(R.id.item_cart_inc);
            mButtonDel = (Button) itemView.findViewById(R.id.item_cart_del);

            mCheckBox.setOnCheckedChangeListener(this);

            mButtonInc.setOnClickListener(this);
            mButtonDec.setOnClickListener(this);
            mButtonDel.setOnClickListener(this);
        }

        void setCartItem(CartItem cartItem) {
            mCartItem = cartItem;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mCartItem != null) {
                mCartItem.setChecked(isChecked);
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            CartOperationEvent event = new CartOperationEvent();
            event.id = id;
            event.item = mCartItem;
            EventBus.getDefault().post(event);
        }
    }
}
