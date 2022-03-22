/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package com.someguyssoftware.treasure2.runestone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.charm.IlluminationCharm;
import com.someguyssoftware.treasure2.enums.Rarity;
import com.someguyssoftware.treasure2.item.TreasureItems;
import com.someguyssoftware.treasure2.util.ResourceLocationUtil;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

/**
 * 
 * @author Mark Gottschling on Jan 15, 2022
 *
 */
public class TreasureRunes {
	private static final Map<ResourceLocation, IRunestone> REGISTRY = new HashMap<>();
	private static final Multimap<Rarity, IRunestone> RARITY_REGISTRY = ArrayListMultimap.create();
	private static final Map<IRunestone, Item> ITEM_REGISTRY = new HashMap<>();

	// RUNE_OF_DAMAGE x2 - SCARCE
	public static IRunestone RUNE_OF_MANA;
	public static IRunestone RUNE_OF_DURABILITY; //	lvl 1 durability
	public static IRunestone RUNE_OF_QUALITY;
	public static IRunestone RUNE_OF_ANGELS;
	public static IRunestone RUNE_OF_ANVIL;				// lvl 3 durability
	public static IRunestone RUNE_OF_EQUIP_AS_MANA;
	public static IRunestone RUNE_OF_PERSISTENCE;
	public static  IRunestone RUNE_OF_SOCKETS;

	// RUNE_OF_EVERLASTING -adornment &  charms are infinite but curse of vanishing is added - MYTHICAL
	// RUNE_OF_REPAIR - grants repairability
	// RUNE_OF_SUSTAIN - add # repairs
	

	// NOTE cannot be static as they rely on Items being created and registered first.
	
	static {
		RUNE_OF_MANA = new ManaRunestone.Builder(ResourceLocationUtil.create("mana_rune"))
				.with($ -> {
					$.lore = I18n.translateToLocal("tooltip.runestone.lore.mana_rune");
					$.rarity = Rarity.SCARCE;
					$.getInvalids().add(IlluminationCharm.ILLUMINATION_TYPE);
				}).build();
		register(RUNE_OF_MANA);
		
		RUNE_OF_DURABILITY = new DurabilityRunestone.Builder(ResourceLocationUtil.create("durability_rune"))
				.with($ -> {
					$.lore = I18n.translateToLocal("tooltip.runestone.lore.durability_rune");
					$.rarity = Rarity.SCARCE;
				}).build();
		register(RUNE_OF_DURABILITY);
		
		RUNE_OF_QUALITY = new QualityRunestone.Builder(ResourceLocationUtil.create("quality_rune"))
				.with($ -> {
					$.lore = I18n.translateToLocal("tooltip.runestone.lore.quality_rune");
					$.rarity = Rarity.RARE;
					$.getInvalids().add(IlluminationCharm.ILLUMINATION_TYPE);
				}).build();
		register(RUNE_OF_QUALITY);
		
		RUNE_OF_EQUIP_AS_MANA = new EquipmentManaRunestone.Builder(ResourceLocationUtil.create("equip_mana_rune"))
				.with($ -> {
					$.lore = I18n.translateToLocal("tooltip.runestone.lore.equip_mana_rune");
					$.rarity = Rarity.RARE;
				}).build();
		register(RUNE_OF_EQUIP_AS_MANA);
		
		RUNE_OF_ANVIL = new AnvilRunestone.Builder(ResourceLocationUtil.create("anvil_rune"))
				.with($ -> {
					$.lore = I18n.translateToLocal("tooltip.runestone.lore.anvil_rune");
					$.rarity = Rarity.EPIC;
				}).build();
		register(RUNE_OF_ANVIL);
		
		RUNE_OF_ANGELS = new AngelsRunestone.Builder(ResourceLocationUtil.create("angels_rune"))
				.with($ -> {
					$.lore = I18n.translateToLocal("tooltip.runestone.lore.angels_rune");
					$.rarity = Rarity.LEGENDARY;
				}).build();
		register(RUNE_OF_ANGELS);
		
		RUNE_OF_PERSISTENCE = new PersistenceRune.Builder(ResourceLocationUtil.create("persistence_rune"))
				.with($ -> {
					$.lore = I18n.translateToLocal("tooltip.runestone.lore.persistence_rune");
					$.rarity = Rarity.EPIC;
				}).build();
		register(RUNE_OF_PERSISTENCE);
		
		RUNE_OF_SOCKETS = new SocketsRune.Builder(ResourceLocationUtil.create("sockets_rune"))
				.with($ -> {
					$.lore = I18n.translateToLocal("tooltip.runestone.lore.sockets_rune");
					$.rarity = Rarity.RARE;
				}).build();
		register(RUNE_OF_SOCKETS);
	}

	/**
	 * 
	 * @param runestone
	 */
	public static void register(IRunestone runestone) {
		Treasure.logger.debug("registering runestone -> {}", runestone.getName());
		if (!REGISTRY.containsKey(runestone.getName())) {        	
			REGISTRY.put(runestone.getName(), runestone);
		}
		if (!RARITY_REGISTRY.containsValue(runestone)) {
			RARITY_REGISTRY.put(runestone.getRarity(), runestone);
		}
	}

	public static Optional<IRunestone> get(ResourceLocation name) {
		if (REGISTRY.containsKey(name)) {
			return Optional.of(REGISTRY.get(name));
		}
		return Optional.empty();
	}
	
	public static List<IRunestone> getByRarity(Rarity rarity	) {
		return (List<IRunestone>) RARITY_REGISTRY.get(rarity);
	}

	/**
	 * 
	 * @return
	 */
	public static List<IRunestone> values() {
		return (List<IRunestone>) REGISTRY.values();
	}
	
	public static void register(IRunestone runestone, Item item) {
		Treasure.logger.debug("registering runestone -> {} to item -> {}", runestone.getName(), item.getRegistryName());
		if (!ITEM_REGISTRY.containsKey(runestone)) {        	
			ITEM_REGISTRY.put(runestone, item);
		}
	}
	
	public static Optional<Item> getItem(IRunestone runestone) {
		return Optional.ofNullable(ITEM_REGISTRY.get(runestone));
	}
}
