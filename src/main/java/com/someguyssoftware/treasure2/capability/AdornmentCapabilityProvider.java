package com.someguyssoftware.treasure2.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class AdornmentCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {

	private final IMagicsInventoryCapability magicsCap;
	private final ICharmableCapability charmableCap;
	private final IDurabilityCapability durabilityCap;
	// TODO add IPouchableCapability
	
	public AdornmentCapabilityProvider() {
		this.magicsCap = new MagicsInventoryCapability(1, 1, 1);
		this.charmableCap = new CharmableCapability(magicsCap);
		this.durabilityCap = new DurabilityCapability(100, 100);
	}
	
	public AdornmentCapabilityProvider(ICharmableCapability charmableCap) {
		this.magicsCap = charmableCap.getMagicsCap();
		this.charmableCap = charmableCap;
		this.durabilityCap = new DurabilityCapability(100, 100);
	}
	
//	public AdornmentCapabilityProvider(IMagicsInventoryCapability magicsCap, ICharmableCapability charmableCap) {
//		this.magicsCap = magicsCap;
//		this.charmableCap = charmableCap;
//		this.durabilityCap = new DurabilityCapability();
//	}

	public AdornmentCapabilityProvider(ICharmableCapability charmableCap, IDurabilityCapability durabilityCap) {
		this.magicsCap = charmableCap.getMagicsCap();
		this.charmableCap = charmableCap;
		this.durabilityCap = durabilityCap;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == TreasureCapabilities.CHARMABLE) {
			return true;
		}
		else if (capability == TreasureCapabilities.DURABILITY) {
			return true;
		}
		else if (capability == TreasureCapabilities.MAGICS) {
			return true;
		}
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == TreasureCapabilities.CHARMABLE) {
			return TreasureCapabilities.CHARMABLE.cast(this.charmableCap);
		}
		else if (capability == TreasureCapabilities.DURABILITY) {
			return TreasureCapabilities.DURABILITY.cast(this.durabilityCap);
		}
		else if (capability == TreasureCapabilities.MAGICS) {
			return TreasureCapabilities.MAGICS.cast(this.magicsCap);
		}
		return null;
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound magicsTag = (NBTTagCompound)	TreasureCapabilities.MAGICS.getStorage().writeNBT(TreasureCapabilities.MAGICS, magicsCap, null);
		NBTTagCompound charmableTag = (NBTTagCompound)TreasureCapabilities.CHARMABLE.getStorage().writeNBT(TreasureCapabilities.CHARMABLE, charmableCap, null);
		NBTTagCompound durabilityTag = (NBTTagCompound)TreasureCapabilities.DURABILITY.getStorage().writeNBT(TreasureCapabilities.DURABILITY, durabilityCap, null);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("magics", magicsTag);
		tag.setTag("charmable", charmableTag);
		tag.setTag("durability", durabilityTag);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("magics")) {
			NBTTagCompound tag = nbt.getCompoundTag("magics");
			TreasureCapabilities.MAGICS.getStorage().readNBT(TreasureCapabilities.MAGICS, magicsCap, null, tag);			
		}
		if (nbt.hasKey("charmable")) {
			NBTTagCompound tag = nbt.getCompoundTag("charmable");
			TreasureCapabilities.CHARMABLE.getStorage().readNBT(TreasureCapabilities.CHARMABLE, charmableCap, null, tag);
		}		
		if (nbt.hasKey("durability")) {
			NBTTagCompound tag = nbt.getCompoundTag("durability");
			TreasureCapabilities.DURABILITY.getStorage().readNBT(TreasureCapabilities.DURABILITY, durabilityCap, null, tag);
		}
	}
}
