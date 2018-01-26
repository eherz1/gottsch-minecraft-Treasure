package com.someguyssoftware.treasure2.client.model;

import com.someguyssoftware.treasure2.lock.LockState;
import com.someguyssoftware.treasure2.tileentity.TreasureChestTileEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * 
 * @author Mark Gottschling on Jan 15, 2018
 *
 */
public class StandardChestModel extends ModelBase implements ITreasureChestModel {

	//fields
	ModelRenderer base;
	ModelRenderer lid;
    ModelRenderer Pad1;
    ModelRenderer Latch1;
    ModelRenderer Latch2;
    ModelRenderer Latch3;
    
	public StandardChestModel() {
		textureWidth = 128;
		textureHeight = 64;

		base = new ModelRenderer(this, 0, 21);
		base.addBox(-7F, 0F, -14F, 14, 10, 14);
		base.setRotationPoint(0F, 14F, 7F);
		base.setTextureSize(64, 32);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
	      lid = new ModelRenderer(this, 0, 0);
	      lid.addBox(-7F, -5F, -14F, 14, 5, 14);
	      lid.setRotationPoint(0F, 15F, 7F);
	      lid.setTextureSize(64, 32);
	      lid.mirror = true;
	      setRotation(lid, 0F, 0F, 0F);
	      Pad1 = new ModelRenderer(this, 7, 47);
	      Pad1.addBox(-2F, 0F, -1.1F, 4, 4, 1); // changed -1F to -1.1F
	      Pad1.setRotationPoint(0F, 15F, -6F);
	      Pad1.setTextureSize(128, 64);
	      Pad1.mirror = true;
	      setRotation(Pad1, 0F, 0F, 0F);
	      Latch1 = new ModelRenderer(this, 0, 47);
	      Latch1.addBox(-1F, -1F, -15F, 2, 3, 1);
	      Latch1.setRotationPoint(0F, 15F, 7F);
	      Latch1.setTextureSize(128, 64);
	      Latch1.mirror = true;
	      setRotation(Latch1, 0F, 0F, 0F);
	      Latch2 = new ModelRenderer(this, 0, 47);
	      Latch2.addBox(0F, -1F, -8F, 1, 3, 2);
	      Latch2.setRotationPoint(7F, 15F, 7F);
	      Latch2.setTextureSize(128, 64);
	      Latch2.mirror = true;
	      setRotation(Latch2, 0F, 0F, 0F);
	      Latch3 = new ModelRenderer(this, 0, 47);
	      Latch3.addBox(-1F, -1F, -8F, 1, 3, 2);
	      Latch3.setRotationPoint(-7F, 15F, 7F);
	      Latch3.setTextureSize(128, 64);
	      Latch3.mirror = true;
	      setRotation(Latch3, 0F, 0F, 0F);
	}

	/**
	 * 
	 */
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		base.render(f5);
		lid.render(f5);
	    Pad1.render(f5);
	    Latch1.render(f5);
	    Latch2.render(f5);
	    Latch3.render(f5);
	}

	/**
	 * 
	 * @param model
	 * @param x
	 * @param y
	 * @param z
	 */
	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

	/**
	 * 
	 */
	@Override
	public void renderAll(TreasureChestTileEntity te) {
		// set the angles of the latch to same as the lib
		Latch1.rotateAngleX = lid.rotateAngleX;
		Latch2.rotateAngleX = lid.rotateAngleX;
		Latch3.rotateAngleX = lid.rotateAngleX;
		
		base.render(0.0625F);
		lid.render(0.0625F);
		Pad1.render(0.0625F);	
		// always render the main latch
		Latch1.render(0.0625F);
		for (LockState state : te.getLockStates()) {
			if (state.getLock() != null) {
				switch(state.getSlot().getIndex()) {
					case 1:
						Latch3.render(0.0625F);
						break;
					case 2:				
						Latch2.render(0.0625F);
						break;
				}
			}
		}
	}

	/**
	 * @return the base
	 */
	public ModelRenderer getBase() {
		return base;
	}

	/**
	 * @param base the base to set
	 */
	public void setBase(ModelRenderer base) {
		this.base = base;
	}

	/**
	 * @return the lid
	 */
	@Override
	public ModelRenderer getLid() {
		return lid;
	}

	/**
	 * @param lid the lid to set
	 */
	public void setLid(ModelRenderer lid) {
		this.lid = lid;
	}

}
