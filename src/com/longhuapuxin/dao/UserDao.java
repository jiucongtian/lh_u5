package com.longhuapuxin.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.longhuapuxin.db.bean.User;

import android.content.Context;

/**
 *@author zh
 *@date 2015-8-28
 */
public class UserDao {
	  
    private Dao<User, Integer> userDaoOpe;  
    private DatabaseHelper helper;  
  
    @SuppressWarnings("unchecked")
	public UserDao(Context context)  
    {  
        try  
        {  
            helper = DatabaseHelper.getHelper(context);  
            userDaoOpe = helper.getDao(User.class);  
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * Add a user.
     */  
    public void add(User user)  
    {  
        try  
        {  
            userDaoOpe.create(user);  
        } catch (SQLException e)  
        {  
            e.printStackTrace();  
        }  
  
    }
    
    public List<User> fetchAll() {
    	List<User> users = new ArrayList<User>();
    	try {
    		users = userDaoOpe.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
		return users;
    }
	
}
