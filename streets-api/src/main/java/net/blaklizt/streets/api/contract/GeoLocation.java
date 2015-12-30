package net.blaklizt.streets.api.contract;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class GeoLocation implements Serializable {

    @NotNull
    Double longitude;
    @NotNull
    Double latitude;

    public GeoLocation(Number longitude, Number latitude) {
        this.longitude = longitude.doubleValue();
        this.latitude = latitude.doubleValue();
    }

    public GeoLocation() {}

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
