package com.doccube.admin;

import com.doccube.core.security.Principal;
import com.doccube.exception.AccessorException;
import com.doccube.meta.EntityAttr;
import com.doccube.meta.EntityConstants;
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
			// TODO Auto-generated catch block
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
		EntityAttr attr = new EntityAttr("i_account", "c0", "account");
		meta.addAttr(attr);
		attr = new EntityAttr("i_domain", "c0", "domain");
		meta.addAttr(attr);
		attr = new EntityAttr("i_name", "c0", "name");
		meta.addAttr(attr);
		attr = new EntityAttr("i_source", "c0", "source");
		meta.addAttr(attr);
		attr = new EntityAttr("i_password", "c0", "password");
		meta.addAttr(attr);
		attr = new EntityAttr("i_groups", EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, "c1", "groups");
		meta.addAttr(attr);
		attr = new EntityAttr("i_roles", EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, "c1", "roles");
		meta.addAttr(attr);
		
		eadmin.setupSchema(meta);

	}

	private void setupGroupSchema() {

		EntityAdmin eadmin = EntityAdmin.getInstance();
		
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_USER_GROUP);
		meta.setSchemaClass(GenericEntity.class.getName());
		meta.setDescription("user Group schema ");
		meta.setTraceable(true);
		EntityAttr attr = new EntityAttr("i_group_name", "c0", "groupname");
		meta.addAttr(attr);
		attr = new EntityAttr("i_users", EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, "c1","users");
		meta.addAttr(attr);
		attr = new EntityAttr("i_groups", EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, "c1","groups");
		meta.addAttr(attr);
		
		eadmin.setupSchema(meta);
	}

	private void setupRoleSchema() {

		EntityAdmin eadmin = EntityAdmin.getInstance();
		
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_USER_ROLE);
		meta.setSchemaClass(GenericEntity.class.getName());
		meta.setDescription("user Group schema ");
		meta.setTraceable(true);
		EntityAttr attr = new EntityAttr("i_role_name", "c0", "rolename");
		meta.addAttr(attr);
		attr = new EntityAttr("i_users", EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, "c1", "users");
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
