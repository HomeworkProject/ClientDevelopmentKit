package de.mlessmann.api.data;

import de.mlessmann.api.annotations.API;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 11.08.16.
 */
public interface IHWCarrier {

    //------------------------- METHODS ---------------------------------

    @API
    JSONObject getJSON();

    //------------------------- Sub classes -----------------------------
    //Builder

    class Builder {

        public static IHWCarrier.Builder builder() { return new IHWCarrier.Builder(); }

        private JSONObject json;
        private IHWCarrier.JSONBuilder jsonBuilder;

        public IHWCarrier.Builder json(JSONObject json) {
            this.json = json;
            return this;
        }

        public IHWCarrier build() {
            return new IHWCarrier() {
                private JSONObject o = json;

                @Override
                public JSONObject getJSON() {
                    return o;
                }
            };
        }

    }

    //JSONBuilder

    class JSONBuilder {

        private String subject = "null";
        private int[] date = new int[]{2000, 1, 1};
        private String id = null;
        private JSONObject l = null;
        private JSONObject s = null;
        private String t;
        private String d;

        public JSONObject build() {
            JSONObject r = new JSONObject();

            JSONArray a = new JSONArray();

            for (int i : date)
                a.put(i);
            r.put("date", a);
            r.put("subject", subject);
            if (id != null)
                r.put("id", id);
            if (t != null)
                r.put("title", t);
            if (d != null)
                r.put("desc", d);
            r.put("type", "homework");
            return r;

        }

        public IHWCarrier.JSONBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public IHWCarrier.JSONBuilder date(int yyyy, int MM, int dd) {
            date = new int[]{yyyy, MM, dd};
            return this;
        }

        public IHWCarrier.JSONBuilder id(String id) {
            this.id = id;
            return this;
        }

        public IHWCarrier.JSONBuilder description(String d) {
            this.d = d;
            return this;
        }

        public IHWCarrier.JSONBuilder title(String t) {
            this.t = t;
            return this;
        }

    }

}
