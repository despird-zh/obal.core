package com.dcube.admin;

import com.dcube.accessor.IAdminGAccessor;
import com.dcube.core.security.Principal;
import com.dcube.exception.AccessorException;
import com.dcube.meta.EntityAttr;
import com.dcube.meta.EntityConstants;
import com.dcube.meta.EntityMeta;
import com.dcube.meta.GenericEntity;
import com.dcube.meta.EntityConstants.GroupEnum;
import com.dcube.meta.EntityConstants.RoleEnum;
import com.dcube.meta.EntityConstants.UserEnum;
import com.dcube.util.AccessorUtils;
import com.dcube.util.EntitieUtils;

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

		IAdminGAccessor aa = ea.getAdminAccessor(princ);

		try {

			EntityMeta infoMeta = EntitieUtils
					.getEntityMeta(EntityConstants.ENTITY_META_INFO);
			aa.createSchema(infoMeta.getEntityName(), infoMeta.getAllAttrs());

			EntityMeta arrtMeta = EntitieUtils
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
		
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_USER);
		meta.setEntityClass(GenericEntity.class.getName());
		meta.setAccessorName(EntityConstants.ACCESSOR_ENTITY_USER);
		meta.setDescription("user schema ");
		meta.setTraceable(true);
		EntityAttr attr = new EntityAttr(UserEnum.Account.attribute, 
				UserEnum.Account.colfamily, 
				UserEnum.Account.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(UserEnum.Domain.attribute, 
				UserEnum.Domain.colfamily, 
				UserEnum.Domain.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(UserEnum.Name.attribute, 
				UserEnum.Name.colfamily, 
				UserEnum.Name.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(UserEnum.Source.attribute, 
				UserEnum.Source.colfamily, 
				UserEnum.Source.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(UserEnum.Password.attribute, 
				UserEnum.Password.colfamily, 
				UserEnum.Password.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(UserEnum.Salt.attribute, 
				UserEnum.Salt.colfamily, 
				UserEnum.Salt.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(UserEnum.Groups.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				UserEnum.Groups.colfamily, 
				UserEnum.Groups.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(UserEnum.Roles.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 				
				UserEnum.Roles.colfamily, 
				UserEnum.Roles.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(UserEnum.Profile.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 				
				UserEnum.Profile.colfamily, 
				UserEnum.Profile.qualifier);
		meta.addAttr(attr);
		
		eadmin.setupSchema(meta);

	}

	private void setupGroupSchema() {

		EntityAdmin eadmin = EntityAdmin.getInstance();
		
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_USER_GROUP);
		meta.setEntityClass(GenericEntity.class.getName());
		meta.setAccessorName(EntityConstants.ACCESSOR_ENTITY_GROUP);
		meta.setDescription("user Group schema ");
		meta.setTraceable(true);
		EntityAttr attr = new EntityAttr(GroupEnum.Name.attribute, 
				GroupEnum.Name.colfamily, 
				GroupEnum.Name.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(GroupEnum.Description.attribute,
				GroupEnum.Description.colfamily, 
				GroupEnum.Description.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(GroupEnum.Parent.attribute,
				GroupEnum.Parent.colfamily, 
				GroupEnum.Parent.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(GroupEnum.Users.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				GroupEnum.Users.colfamily, 
				GroupEnum.Users.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(GroupEnum.Groups.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				GroupEnum.Groups.colfamily, 
				GroupEnum.Groups.qualifier);
		meta.addAttr(attr);
		
		eadmin.setupSchema(meta);
	}

	private void setupRoleSchema() {

		EntityAdmin eadmin = EntityAdmin.getInstance();
		
		EntityMeta meta = new EntityMeta(EntityConstants.ENTITY_USER_ROLE);
		meta.setEntityClass(GenericEntity.class.getName());
		meta.setAccessorName(EntityConstants.ACCESSOR_ENTITY_ROLE);
		meta.setDescription("user Group schema ");
		meta.setTraceable(true);
		
		EntityAttr attr = new EntityAttr(RoleEnum.Name.attribute, 
				RoleEnum.Name.colfamily, 
				RoleEnum.Name.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(RoleEnum.Description.attribute,
				RoleEnum.Description.colfamily, 
				RoleEnum.Description.qualifier);
		meta.addAttr(attr);
		
		attr = new EntityAttr(RoleEnum.Users.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				RoleEnum.Users.colfamily, 
				RoleEnum.Users.qualifier);
		meta.addAttr(attr);

		attr = new EntityAttr(RoleEnum.Groups.attribute, EntityAttr.AttrMode.MAP, EntityAttr.AttrType.STRING, 
				RoleEnum.Groups.colfamily, 
				RoleEnum.Groups.qualifier);
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
		eadmin.dropSchema(EntityConstants.ENTITY_USER);
		eadmin.dropSchema(EntityConstants.ENTITY_META_INFO);
		eadmin.dropSchema(EntityConstants.ENTITY_META_ATTR);
	}
}
