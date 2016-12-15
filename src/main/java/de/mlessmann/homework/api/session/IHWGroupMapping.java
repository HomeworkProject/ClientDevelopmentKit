package de.mlessmann.homework.api.session;

import de.mlessmann.common.annotations.API;
import de.mlessmann.common.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Life4YourGames on 21.09.16.
 */
@API
public interface IHWGroupMapping {

    List<String> getGroups();

    Map<String, List<String>> getMapping();

    @Nullable
    List<String> getUsersFor(String groupName);

    JSONObject getJSON();
}