package de.homeworkproject.homework.internal.homework;

import de.homeworkproject.homework.api.session.IHWGroupMapping;
import de.mlessmann.common.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by Life4YourGames on 21.09.16.
 */
public class HWGroupMapping implements IHWGroupMapping {

    private JSONObject json;

    public HWGroupMapping(JSONObject json) {
        this.json = json;
    }

    @Override
    public JSONObject getJSON() {
        return json;
    }

    @Override
    public Map<String, List<String>> getMapping() {
        Map<String, List<String>> mapping = new HashMap<String, List<String>>();

        //ForEach not possible due to the org.json lib in android
        Iterator<String> i = json.keys();
        while (i.hasNext()) {
            String k = i.next();
            JSONArray a = json.optJSONArray(k);
            if (a!=null) {
                List<String> users = new ArrayList<String>();
                for (int I = 0; I<a.length(); I++) {
                    Object o = a.get(I);
                    if (o instanceof String)
                        users.add(((String) o));
                }
                mapping.put(k, users);
            }
        }
        return mapping;
    }

    @Override
    public List<String> getGroups() {
        List<String> groups = new ArrayList<String>();

        Iterator<String> i = json.keys();
        while (i.hasNext()) {
            String k = i.next();
            JSONArray a = json.optJSONArray(k);
            if (a!=null) {
                groups.add(k);
            }
        }
        return groups;
    }

    @Nullable
    @Override
    public List<String> getUsersFor(String groupName) {
        List<String> users = null;

        Iterator<String> i = json.keys();
        while (i.hasNext()) {
            String k = i.next();
            if(!k.equals(groupName))
                continue;
            JSONArray a = json.optJSONArray(k);
            if (a!=null) {
                users = new ArrayList<String>();
                for (int I = 0; I<a.length(); I++) {
                    Object o = a.get(I);
                    if (o instanceof String)
                        users.add(((String) o));
                }
                break;
            }
        }
        return users;
    }
}
