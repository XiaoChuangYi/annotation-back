package com.malgo.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cjl on 2018/5/27.
 */
public class JpaUpdateUtil {

    public static String[] getNullProperties(Object src) {
        BeanWrapper srcBean = new BeanWrapperImpl(src);
        PropertyDescriptor[] propertyDescriptors = srcBean.getPropertyDescriptors();
        Set<String> emptyName = new HashSet<>();
        Arrays.stream(propertyDescriptors).forEach(x -> {
                    if (srcBean.getPropertyValue(x.getName()) == null)
                        emptyName.add(x.getName());
                }
        );
        String [] result=new String[emptyName.size()];
        return emptyName.toArray(result);
    }
}
