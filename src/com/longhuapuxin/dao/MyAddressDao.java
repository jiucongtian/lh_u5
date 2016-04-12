package com.longhuapuxin.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.longhuapuxin.db.bean.MyAddress;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by asus on 2016/1/6.
 */
public class MyAddressDao {
    private Dao<MyAddress, Integer> myAddressDaoOpe;
    private DatabaseHelper helper;

    @SuppressWarnings("unchecked")
    public MyAddressDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            myAddressDaoOpe = helper.getDao(MyAddress.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(MyAddress myAddress) {
        try {
            myAddressDaoOpe.create(myAddress);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(MyAddress myAddress) {
        try {
            myAddressDaoOpe.update(myAddress);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(MyAddress myAddress) {
        try {
            myAddressDaoOpe.delete(myAddress);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MyAddress> fetchAll() {
        List<MyAddress> list = new ArrayList<MyAddress>();
        try {
            list = myAddressDaoOpe.queryForAll();
            for (int i = 0; i < list.size(); i++
                    ) {
                if (list.get(i).isDefault()) {
                    Collections.swap(list, 0, i);
                    for (int j = 1; j < i; j++) {
                        Collections.swap(list, j, i);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public MyAddress get(int i) {
        List<MyAddress> list = new ArrayList<MyAddress>();
        MyAddress item = new MyAddress();
        try {
            list = myAddressDaoOpe.queryForAll();
            for (MyAddress address :
                    list) {
                if (address.getId() == i) {
                    item = address;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item;
    }
}
