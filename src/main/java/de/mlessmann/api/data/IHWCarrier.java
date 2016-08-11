package de.mlessmann.api.data;

import de.mlessmann.api.annotations.API;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 11.08.16.
 */
public interface IHWCarrier {

    //------------------------- METHODS ---------------------------------
    static IHWCarrier.Builder builder() {
        return new IHWCarrier.Builder();
    }

    @API
    JSONObject getJSON();

    //------------------------- Sub classes -----------------------------
    //Builder

    class Builder {

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

        public JSONObject build() {
            JSONObject r = new JSONObject();

            JSONArray a = new JSONArray();

            for (int i : date)
                a.put(i);
            r.put("date", a);
            r.put("subject", subject);
            if (id != null)
                r.put("id", id);
            if (l == null)
                l = new JSONObject();
            r.put("long", l);
            if (s == null)
                s = new JSONObject();
            r.put("short", s);

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

        public IHWCarrier.JSONBuilder description(String desc) {
            shortDescription(desc);
            longDescription(desc);
            return this;
        }

        public IHWCarrier.JSONBuilder shortDescription(String desc) {
            if (s == null)
                s = new JSONObject();
            s.put("desc", desc);
            return this;
        }

        public IHWCarrier.JSONBuilder longDescription(String desc) {
            if (l == null)
                l = new JSONObject();
            l.put("desc", desc);
            return this;
        }

        public IHWCarrier.JSONBuilder longObj(JSONObject l) {
            this.l = l;
            return this;
        }

        public IHWCarrier.JSONBuilder shortObj(JSONObject s) {
            this.s = s;
            return this;
        }

    }

}
