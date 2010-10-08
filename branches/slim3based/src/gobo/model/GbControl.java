package gobo.model;

import java.io.Serializable;
import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;

@Model(kind = "GOBO_CONTROL")
public class GbControl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version = 0L;

    // kind name
    private String kindName;

    // processed count
    private Integer count;

    // email to report
    private Email reportTo;

    // AuthSubToken
    private String authSubToken;
    
    // Google Spreadsheet's key
    private String ssKey;
    
    // tableId in the Google Spreadsheet.
    private String tableId;
    
    // cursor
    private String cursor;
    
    // update time
    private Date date;
    
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((key == null) ? 0
                                 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GbControl other = (GbControl) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }


    public Key getKey() {
        return key;
    }
    public void setKey(Key key) {
        this.key = key;
    }

    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }

	/**
	 * @param kindName the wsTitle to set
	 */
	public void setKindName(String kindName) {
		this.kindName = kindName;
	}

	/**
	 * @return the wsTitle
	 */
	public String getKindName() {
		return kindName;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param reportTo the reportTo to set
	 */
	public void setReportTo(Email reportTo) {
		this.reportTo = reportTo;
	}

	/**
	 * @return the reportTo
	 */
	public Email getReportTo() {
		return reportTo;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param ssKey the ssKey to set
	 */
	public void setSsKey(String ssKey) {
		this.ssKey = ssKey;
	}

	/**
	 * @return the ssKey
	 */
	public String getSsKey() {
		return ssKey;
	}

	/**
	 * @param tableId the tableId to set
	 */
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	/**
	 * @return the tableId
	 */
	public String getTableId() {
		return tableId;
	}

	/**
	 * @param authSubToken the authSubToken to set
	 */
	public void setAuthSubToken(String authSubToken) {
		this.authSubToken = authSubToken;
	}

	/**
	 * @return the authSubToken
	 */
	public String getAuthSubToken() {
		return authSubToken;
	}

	/**
	 * @param cursor the cursor to set
	 */
	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	/**
	 * @return the cursor
	 */
	public String getCursor() {
		return cursor;
	}
	
}