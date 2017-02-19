package mobi.vhly.supershop;

import android.database.DataSetObserver;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import mobi.vhly.supershop.adapters.CartListAdapter;
import mobi.vhly.supershop.event.CartOperationEvent;
import mobi.vhly.supershop.model.CartItem;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button mButtonEdit;

    private List<CartItem> mItems;

    private CartListAdapter mAdapter;

    private CartDataObserver mCartDataObserver;

    private TextView mTextTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);

        mButtonEdit = (Button) findViewById(R.id.btn_edit);
        mButtonEdit.setOnClickListener(this);

        mTextTotal = (TextView) findViewById(R.id.txt_total);

        ListView listView = (ListView) findViewById(R.id.cart_list);
        if (listView != null) {
            mItems = new ArrayList<>();

            for (int i = 0; i < 30; i++) {
                CartItem item = new CartItem();
                item.setCount(1);
                item.setProductId(i);
                item.setProductName("7天精通Android开发 - " + i);
                item.setProductPrice(30 + i);
                mItems.add(item);
            }

            mAdapter = new CartListAdapter(this, mItems);
            listView.setAdapter(mAdapter);

            // Adapter 可以指定数据观察者，每次 notifyDataSetChange 观察者自动调用
            mCartDataObserver = new CartDataObserver();
            mAdapter.registerDataSetObserver(mCartDataObserver);

            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        for (CartItem item : mItems) {
            item.setChecked(false);
        }
        mAdapter.switchEditMode();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartOperation(CartOperationEvent event) {
        int count = event.item.getCount();
        switch (event.id) {
            case R.id.item_cart_inc:
                count++;
                event.item.setCount(count);
                break;
            case R.id.item_cart_dec:
                count--;
                if (count > 0) {
                    event.item.setCount(count);
                }
                break;
            case R.id.item_cart_del:
                mItems.remove(event.item);
                break;
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        mAdapter.unregisterDataSetObserver(mCartDataObserver);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 点击条目，查看商品详情
        Snackbar.make(parent, "点击条目 " + position, Snackbar.LENGTH_SHORT).show();
    }

    private class CartDataObserver extends DataSetObserver{
        @Override
        public void onChanged() {
            // TODO: 计算金额

            float total = 0;
            for (CartItem item : mItems) {
                total = total + (item.getCount() * item.getProductPrice());
            }

            mTextTotal.setText(String.format("¥ %.2f元", total));
        }
    }
}
