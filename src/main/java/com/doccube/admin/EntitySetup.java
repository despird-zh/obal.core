package com.doccube.admin;

import com.doccube.core.security.Principal;
import com.doccube.exception.AccessorException;
import com.doccube.meta.EntityAttr;
import com.doccube.meta.EntityConstants;
import com.doccube.meta.EntityConstants.UserInfo;
import com.doccube.meta.EntityConstants.GroupInfo;
import com.doccube.meta.EntityConstants.RoleInfo;
import com.doccube.meta.EntityMeta;
import com.doccube.meta.GenericEntity;
import com.doccube.util.AccessorUtils;
import com.doccube.util.EntityUtils;

/**
 * EntitySetup prepare the installation of doccube package.
 * 
 * @author despird-zh
 * @version 0.1 2014-3-2
 * 
 **/
public class EntitySetup {

	public EntitySetup() {}

	/**
	 * Setup the necessary schemas 
	 **/
	public void setup() {

		setupMetaSchema();
		setupUserSchema();
		setupGroupSchema();
		setupRoleSchema();
	}

	private void setupMetaSchema() {

		EntityAdmin ea = EntityAdmin.getInstance();
		Principal princ = new Principal("acc", "demo", "pwd");

		IAdminAccessor aa = ea.getAdminAccessor(princ);

		try {

			EntityMeta infoMeta = EntityUtils
					.getEntityMeta(EntityConstants.ENTITY_META_INFO);
			aa.createSchema(infoMeta.getEntityName(), infoMeta.getAllAttrs());

			EntityMeta arrtMeta = EntityUtils
					.getEntityMeta(EntityConstants.ENTITY_META_ATTR);
			aa.createSchema(arrtMeta.getEntityName(), arrtMeta.getAllAttrs());

		} catch (AccessorException e) {
			
			e.printStackTrace();
		} finally {

			AccessorUtils.closeAccessor(aa);
		}
	}

	private void setupUserSchema() {

		EntityAdmin eadmin = EntityAdmin.getInstance();
		
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_PRINCIPAL);
		meta.setSchemaClass(GenericEntity.class.getName());
		meta.setDescription("user schema ");
		meta.setTraceable(true);
		EntityAttr attr = new EntityAttr(UserInfo.Account.attribute, 
				UserInfo.Account.colfamily, 
				UserInfo.Account.qualifier);
		meta.addAttr(attr);
		attr = new EntityAttr(UserInfo.Domain.attribute, 
				UserInfo.Domain.colfamily, 
				UserInfo.Domain.qualifier);
		meta.addAttr(attr);
		attr = new EntityAttr(UserInfo.Name.attribute, 
				UserInfo.Name.colfamily, 
				UserInfo.Name.qualifier);
		meta.addAttr(attr);
		attr = new EntityAttr(UserInfo.Source.attribute, 
				UserInfo.Source.colfamily, 
				UserInfo.Source.qualifier);
		meta.addAttr(attr);
		attr = new EntityAttr(UserInfo.Password.attribute, 
				UserInfo.Password.colfamily, 
				UserInfo.Password.qualifier);
		meta.addAttr(attr);
		attr = new EntityAttr(UserInfo.Salt.attribute, 
				UserInfo.Salt.colfamily, 
				UserInfo.Salt.qualifier);
		meta.addAttr(attr);
		attr = new EntityAttr(UserInfo.Groups.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				UserInfo.Groups.colfamily, 
				UserInfo.Groups.qualifier);
		meta.addAttr(attr);
		attr = new EntityAttr(UserInfo.Roles.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 				
				UserInfo.Roles.colfamily, 
				UserInfo.Roles.qualifier);
		meta.addAttr(attr);
		
		eadmin.setupSchema(meta);

	}

	private void setupGroupSchema() {

		EntityAdmin eadmin = EntityAdmin.getInstance();
		
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_USER_GROUP);
		meta.setSchemaClass(GenericEntity.class.getName());
		meta.setDescription("user Group schema ");
		meta.setTraceable(true);
		EntityAttr attr = new EntityAttr(GroupInfo.Name.attribute, 
				GroupInfo.Name.colfamily, 
				GroupInfo.Name.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(GroupInfo.Users.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				GroupInfo.Users.colfamily, 
				GroupInfo.Users.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(GroupInfo.Groups.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				GroupInfo.Groups.colfamily, 
				GroupInfo.Groups.qualifier);
		meta.addAttr(attr);
		
		eadmin.setupSchema(meta);
	}

	private void setupRoleSchema() {

		EntityAdmin eadmin = EntityAdmin.getInstance();
		
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_USER_ROLE);
		meta.setSchemaClass(GenericEntity.class.getName());
		meta.setDescription("user Group schema ");
		meta.setTraceable(true);
		
		EntityAttr attr = new EntityAttr(RoleInfo.Name.attribute, 
				RoleInfo.Name.colfamily, 
				RoleInfo.Name.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(RoleInfo.Users.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				RoleInfo.Users.colfamily, 
				RoleInfo.Users.qualifier);
		meta.addAttr(attr);

		eadmin.setupSchema(meta);
	}

	/**
	 * Purge the predefined schemas(tables) 
	 **/
	public void purge(){
		
		EntityAdmin eadmin = EntityAdmin.getInstance();
		eadmin.dropSchema(EntityConstants.ENTITY_USER_ROLE);
		eadmin.dropSchema(EntityConstants.ENTITY_USER_GROUP);
		eadmin.dropSchema(EntityConstants.ENTITY_PRINCIPAL);
		eadmin.dropSchema(EntityConstants.ENTITY_META_INFO);
		eadmin.dropSchema(EntityConstants.ENTITY_META_ATTR);
	}
}
