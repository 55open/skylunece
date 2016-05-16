package com.model;
 
import com.skylucene.core.annotation.SkyDocumentId;
import com.skylucene.core.annotation.SkyField;
import com.skylucene.core.annotation.SkyIndexed;
import com.skylucene.core.annotation.SkyNumericField;
import com.skylucene.core.annotation.SkyField.Analyze;
import com.skylucene.core.annotation.SkyField.Index;
import com.skylucene.core.annotation.SkyField.Store;

@SkyIndexed
public class User {
    
    private Long id;
    private String username;
    private String password;
    private Integer isDelete;
    
    @SkyDocumentId
    @SkyNumericField
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @SkyField(analyze =Analyze.YES, store = Store.YES)
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @SkyField(analyze =Analyze.NO, store = Store.YES)
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getIsDelete() {
        return isDelete;
    }
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
    @Override
    public String toString() {
	return "User [id=" + id + ", username=" + username + ", password="
		+ password + ", isDelete=" + isDelete + "]";
    }
    
    

}
