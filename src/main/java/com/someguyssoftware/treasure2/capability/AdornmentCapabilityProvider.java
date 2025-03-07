package com.someguyssoftware.treasure2.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class AdornmentCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {

	private final ICharmableCapability charmableCap;
	private final IDurabilityCapability durabilityCap;
	private final IRunestonesCapability runestonesCap;
	// TODO add IPouchableCapability
	
	public AdornmentCapabilityProvider() {
		this.charmableCap = new CharmableCapability(0, 0, 0);
		this.durabilityCap = new DurabilityCapability(100, 100);
		this.runestonesCap = new RunestonesCapability(0, 0, 0);
	}
	
	@Deprecated
	public AdornmentCapabilityProvider(ICharmableCapability charmableCap) {
		this.charmableCap = charmableCap;
		this.durabilityCap = new DurabilityCapability(100, 100);
		this.runestonesCap = new RunestonesCapability(0, 0, 0);
	}

	public AdornmentCapabilityProvider(ICharmableCapability charmableCap, IDurabilityCapability durabilityCap) {
		this.charmableCap = charmableCap;
		this.durabilityCap = durabilityCap;
		this.runestonesCap = new RunestonesCapability(0, 0, 0);
	}
	
	public AdornmentCapabilityProvider(ICharmableCapability charmableCap, IRunestonesCapability runestonesCap, IDurabilityCapability durabilityCap) {
		this.charmableCap = charmableCap;
		this.runestonesCap = runestonesCap;
		this.durabilityCap = durabilityCap;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == TreasureCapabilities.CHARMABLE) {
			return true;
		}
		else if (capability == TreasureCapabilities.RUNESTONES) {
			return true;
		}
		else if (capability == TreasureCapabilities.DURABILITY) {
			return true;
		}
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == TreasureCapabilities.CHARMABLE) {
			return TreasureCapabilities.CHARMABLE.cast(this.charmableCap);
		}
		else if (capability == TreasureCapabilities.RUNESTONES) {
			return TreasureCapabilities.RUNESTONES.cast(this.runestonesCap);
		}
		else if (capability == TreasureCapabilities.DURABILITY) {
			return TreasureCapabilities.DURABILITY.cast(this.durabilityCap);
		}
		return null;
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound charmableTag = (NBTTagCompound)TreasureCapabilities.CHARMABLE.getStorage().writeNBT(TreasureCapabilities.CHARMABLE, charmableCap, null);
		NBTTagCompound runestonesTag = (NBTTagCompound)TreasureCapabilities.RUNESTONES.getStorage().writeNBT(TreasureCapabilities.RUNESTONES, runestonesCap, null);
		NBTTagCompound durabilityTag = (NBTTagCompound)TreasureCapabilities.DURABILITY.getStorage().writeNBT(TreasureCapabilities.DURABILITY, durabilityCap, null);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("charmable", charmableTag);
		tag.setTag("runestones", runestonesTag);
		tag.setTag("durability", durabilityTag);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("charmable")) {
			NBTTagCompound tag = nbt.getCompoundTag("charmable");
			TreasureCapabilities.CHARMABLE.getStorage().readNBT(TreasureCapabilities.CHARMABLE, charmableCap, null, tag);
		}
		if (nbt.hasKey("runestones")) {
			NBTTagCompound tag = nbt.getCompoundTag("runestones");
			TreasureCapabilities.RUNESTONES.getStorage().readNBT(TreasureCapabilities.RUNESTONES, runestonesCap, null, tag);
		}
		if (nbt.hasKey("durability")) {
			NBTTagCompound tag = nbt.getCompoundTag("durability");
			TreasureCapabilities.DURABILITY.getStorage().readNBT(TreasureCapabilities.DURABILITY, durabilityCap, null, tag);
		}
	}
}
