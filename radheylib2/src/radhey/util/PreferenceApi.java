/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radhey.util;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author hoshi
 */
public class PreferenceApi {
    Class rootClass;
    PreferenceType type;
    Preferences prefs;

    public PreferenceType getType() {
        return type;
    }

    public PreferenceApi(Class rootClass, PreferenceType type){
        this.rootClass=rootClass;
        if(type==PreferenceType.system)
            prefs=Preferences.systemNodeForPackage(rootClass);
        else
            prefs=Preferences.userNodeForPackage(rootClass);
        this.type=type;
    }

    public String get(String key){
        return prefs.get(key,null);
    }
    
    public String get(String key,String defaultValue){
        return prefs.get(key,defaultValue);
    }

    public String getKey(String value) throws BackingStoreException{
        if(value==null) return null;
        String[] keys=null;
        keys=prefs.keys();
        for(String key:keys){
            if(prefs.get(key, "").equals(value))
                return key;
        }
        return null;
    }

    public void put(String key,String value){
        prefs.put(key, value);
        try{prefs.flush();}catch(Exception ex){}
    }
    public void remove(String key){
        prefs.remove(key);
        try{prefs.flush();}catch(Exception ex){}
    }

}
