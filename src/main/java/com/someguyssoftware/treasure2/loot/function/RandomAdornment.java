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
package com.someguyssoftware.treasure2.loot.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.treasure2.Treasure;
import com.someguyssoftware.treasure2.adornment.TreasureAdornments;
import com.someguyssoftware.treasure2.capability.TreasureCapabilities;
import com.someguyssoftware.treasure2.item.Adornment;
import com.someguyssoftware.treasure2.material.CharmableMaterial;
import com.someguyssoftware.treasure2.material.TreasureCharmableMaterials;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

/**
 * 
 * @author Mark Gottschling on Aug 31, 2021
 *
 */
public class RandomAdornment extends LootFunction {
	private static final ResourceLocation LOCATION = new ResourceLocation("treasure2:random_adornment");
	private static final String LEVELS = "levels";
	private static final String MATERIALS = "materials";

	// the type of adornment - ring, necklace, bracelet, earrings, pocket watch
	//private String adornmentType;

	private RandomValueRange levels;

	// the base material of the adornment to be selected
	//private String material;
	private Optional<List<CharmableMaterial>> materials;


	/**
	 * 
	 * @param conditions
	 * @param charms
	 */
	public RandomAdornment(LootCondition[] conditions) {
		super(conditions);
	}

	public RandomAdornment(LootCondition[] conditions, RandomValueRange levels, Optional<List<CharmableMaterial>> materials) {
		super(conditions);
		this.levels = levels;
		this.materials = materials;
	}

	@Override
	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		Random random = new Random();

		// select random level
		int level = this.levels == null ? 1 : this.levels.generateInt(rand);

		// select material
		CharmableMaterial material = null;
		if (this.materials == null || !this.materials.isPresent()) {
			material = TreasureCharmableMaterials.getBaseMaterial(stack.getCapability(TreasureCapabilities.CHARMABLE, null).getBaseMaterial()).get();
		}
		else {
			material = this.materials.get().get(random.nextInt(materials.get().size()));
		}

		// update level if level exceeds max of material
		if (level > material.getMaxLevel()) {
			level = material.getMaxLevel();
		}

		final int lambdaLevel = level;
		// TODO select all adornments that meet the level and material criteria - this includes all material + gem combos.
		// ex at this point: level = 3, material = silver, so all silver rings, necklaces, bracelets where level <= 3 (or should be == 3 ?)
		List<Adornment> adornmentsByMaterial = TreasureAdornments.getByMaterial(material);
		List<Adornment> 	adornments = adornmentsByMaterial.stream()
			.filter(a -> {
				ItemStack itemStack = new ItemStack(a);
				if (itemStack.getCapability(TreasureCapabilities.CHARMABLE, null).getMaxCharmLevel() == lambdaLevel) {
					return true;
				}
				return false;
			}).collect(Collectors.toList());

		// create a new adornment item
		ItemStack adornment;
		if (adornments == null || adornments.isEmpty()) {
			adornment = stack;
		}
		else {
			adornment = new ItemStack(adornments.get(random.nextInt(adornments.size())));
		}

		return adornment;
	}
	
	/**
	 * 
	 * @author Mark Gottschling on Feb 27, 2022
	 *
	 */
	public static class Serializer extends LootFunction.Serializer<RandomAdornment> {
		public Serializer() {
			super(LOCATION, RandomAdornment.class);
		}

		/**
		 * 
		 */
		public void serialize(JsonObject json, RandomAdornment value, JsonSerializationContext context) {
			json.add(LEVELS, context.serialize(value.levels));

			// serialize the materials
			if (value.materials.isPresent()) {
				final JsonArray jsonArray = new JsonArray();
				value.materials.get().forEach(material -> {
					jsonArray.add(new JsonPrimitive(material.getName().toString()));
				});
				json.add(MATERIALS, jsonArray);
				// json.add(MATERIAL, new JsonPrimitive(value.material.getName().toString()));
			}
		}

		/**
		 * 
		 */
		public RandomAdornment deserialize(JsonObject json, JsonDeserializationContext deserializationContext,
				LootCondition[] conditionsIn) {
			
			RandomValueRange levels = null;
			if (json.has(LEVELS)) {
				 levels = JsonUtils.deserializeClass(json, LEVELS, deserializationContext, RandomValueRange.class);	
			}
			// TODO potential create new RandomValueRange(1)

			Optional<List<CharmableMaterial>> materials = Optional.empty();
			if (json.has(MATERIALS)) {
				// String materialName = JsonUtils.getString(json, MATERIAL);
				// material = TreasureCharmableMaterials.getBaseMaterial(new ResourceLocation(materialName));
				for (JsonElement element : JsonUtils.getJsonArray(json, MATERIALS)) {
					String materialName = JsonUtils.getString(element, "material");
					Optional<CharmableMaterial> material = TreasureCharmableMaterials.getBaseMaterial(new ResourceLocation(materialName));
					if (material.isPresent()) {
						if (!materials.isPresent()) {
							materials = Optional.of(new ArrayList<CharmableMaterial>());
						}
						materials.get().add(material.get());
					}
					else {
						Treasure.logger.warn("Unknown material '{}'", materialName);
					}
				}
			}

			// NOTE no default value for material as it is an optional value

			return new RandomAdornment(conditionsIn, levels, materials);
		}
	}
}