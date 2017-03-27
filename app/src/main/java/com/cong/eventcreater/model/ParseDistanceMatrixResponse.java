package com.cong.eventcreater.model;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by admin on 22/09/2016.
 */

public class ParseDistanceMatrixResponse {
    class DistanceMatrixResponse {
        private String topStatus;
        private String elementStatus;
        private int duration;

        public DistanceMatrixResponse() {
            this.topStatus = "INVALID";
            this.elementStatus = "INVALID";
        }
        public int getDuration() {
            return duration;
        }


        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getElementStatus() {
            return elementStatus;
        }

        public void setElementStatus(String elementStatus) {
            this.elementStatus = elementStatus;
        }

        public String getTopStatus() {
            return topStatus;
        }

        public void setTopStatus(String topStatus) {
            this.topStatus = topStatus;
        }
    }

    private DistanceMatrixResponse distanceMatrixResponse;

    public ParseDistanceMatrixResponse() {
        this.distanceMatrixResponse = new DistanceMatrixResponse();
    }

    public DistanceMatrixResponse getDistanceMatrixResponse() {
        return distanceMatrixResponse;
    }

    public void readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            parseRows(reader);
        } finally {
            reader.close();
        }
    }

    private void parseRows(JsonReader reader) throws IOException {
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("rows")) {
                parseRowArray(reader);
            } else if(name.equals("status")) {
                this.distanceMatrixResponse.setTopStatus(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void parseRowArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while(reader.hasNext()) {
            readRow(reader);
        }
        reader.endArray();
    }

    private void readRow(JsonReader reader) throws IOException {
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("elements")) {
                readElementsArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readElementsArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readElement(reader);
        }
        reader.endArray();
    }

    private void readElement(JsonReader reader) throws IOException {
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("duration")) {
                readDuration(reader);
            } else if(name.equals("status")) {
                this.distanceMatrixResponse.setElementStatus(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readDuration(JsonReader reader) throws IOException {
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("value")) {
                this.distanceMatrixResponse.setDuration(reader.nextInt());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }
}
