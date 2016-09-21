package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWGroupMapping;
import de.mlessmann.common.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        while (json.keys().hasNext()) {
            String k = json.keys().next();
            JSONArray a = json.optJSONArray(k);
            if (a!=null) {
                List<String> users = new ArrayList<String>();
                for (int i = 0; i<a.length(); i++) {
                    Object o = a.get(i);
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

        while (json.keys().hasNext()) {
            String k = json.keys().next();
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

        while (json.keys().hasNext()) {
            String k = json.keys().next();
            if(!k.equals(groupName))
                continue;
            JSONArray a = json.optJSONArray(k);
            if (a!=null) {
                users = new ArrayList<String>();
                for (int i = 0; i<a.length(); i++) {
                    Object o = a.get(i);
                    if (o instanceof String)
                        users.add(((String) o));
                }
                break;
            }
        }
        return users;
    }
}
