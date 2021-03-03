package com.igteam.immersivegeology.common.world.layer.wld;

import com.google.common.collect.Maps;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;
import com.igteam.immersivegeology.common.world.layer.BiomeLayerData;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.layer.BiomeLayer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WorldLayerData
{

	public final static WorldLayerData instance = new WorldLayerData();

	public HashMap<String, BiomeLayerData> worldLayerData = new HashMap<String, BiomeLayerData>();

	public IGBaseBlock LIMESTONE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material);
	public IGBaseBlock MARBLE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material);
	public IGBaseBlock RHYOLITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material);
	public IGBaseBlock BASALT = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material);
	public IGBaseBlock GRANITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material);
	public IGBaseBlock PEGMATITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegmatite.material);
	public IGBaseBlock GABBROS = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material);

	public IGBaseBlock KAOLINITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Kaolinite.material);
	public IGBaseBlock DOLOMITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Dolomite.material);

	public IGBaseBlock NETHERRACK = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Netherrack.material);

	public WorldLayerData()
	{
		initialize();
	}

	public void initialize() {
		buildMountainData();
		buildOceanData();
		buildLowBiomeData();
		buildMediumBiomeData();
		buildNetherBiomeData();
	}

	private void addBiomeData(IGBiome biome, BiomeLayerData data){
		worldLayerData.put(biome.getDisplayName().toString(), data);
	}

	private void buildNetherBiomeData(){

		//Mantle Biome
		addBiomeData(IGBiomes.MANTLE,BiomeLayerBuilder.create(IGBiomes.MANTLE)
						.addLayerData(NETHERRACK)
						.addLayerOreData(1,   EnumMaterials.Gold,0.2f, EnumMaterials.Pyrolusite,0.2f)
						.build());
	}

	private void buildMediumBiomeData() {

		addBiomeData(IGBiomes.BADLANDS,
				BiomeLayerBuilder.create(IGBiomes.BADLANDS).addLayerData(GRANITE, GRANITE, GABBROS, GABBROS, BASALT, BASALT)
						.addLayerOreData(4, EnumMaterials.Gold, 0.45f,EnumMaterials.Hematite, 0.45f)
						.addLayerOreData(3, EnumMaterials.Gold, 0.5f, EnumMaterials.Hematite, 0.45f)
						.addLayerOreData(2, EnumMaterials.Uraninite, 0.25f, EnumMaterials.Ilmenite, 0.38f, EnumMaterials.Hubnerite, 0.35f)
						.addLayerOreData(1, EnumMaterials.Cuprite, 0.30f, EnumMaterials.Hematite, 0.45f, EnumMaterials.Gold, 0.55f)
								.build());

		addBiomeData(IGBiomes.PLATEAU,
				BiomeLayerBuilder.create(IGBiomes.PLATEAU).addLayerData(KAOLINITE, LIMESTONE, DOLOMITE, GRANITE, GABBROS, BASALT)
						.addLayerOreData(1, EnumMaterials.Magnetite, 0.13f, EnumMaterials.Hematite, 0.13f)
						.addLayerOreData(2, EnumMaterials.Vanadinite, 0.23f)
						.addLayerOreData(3, EnumMaterials.Vanadinite, 0.23f)
						.addLayerOreData(4, EnumMaterials.Vanadinite, 0.23f)
						.addLayerOreData(5, EnumMaterials.Vanadinite, 0.23f)
						.addLayerOreData(6, EnumMaterials.Vanadinite, 0.23f)
				.build());

		addBiomeData(IGBiomes.CANYONS,
				BiomeLayerBuilder.create(IGBiomes.CANYONS).addLayerData(LIMESTONE,LIMESTONE,GRANITE,GABBROS,BASALT)
					.addLayerOreData(4,EnumMaterials.Magnetite,0.2f,EnumMaterials.Cuprite,0.3f)
					.addLayerOreData(3,EnumMaterials.Magnetite,0.2f,EnumMaterials.Cuprite,0.3f)
					.addLayerOreData(2,EnumMaterials.Magnetite,0.2f,EnumMaterials.Cuprite,0.3f)
					.addLayerOreData(1,EnumMaterials.Magnetite,0.2f,EnumMaterials.Cuprite,0.3f,EnumMaterials.Chromite,0.2f)
						.build());
	}

	private void buildLowBiomeData() {
		addBiomeData(IGBiomes.DESERT,
				//Desert
				BiomeLayerBuilder.create(IGBiomes.DESERT).addLayerData(GRANITE, PEGMATITE, GRANITE, RHYOLITE, BASALT)
						.addLayerOreData(5, EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f)
						.addLayerOreData(4, EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f,
								EnumMaterials.Uraninite, 0.15f)
						.addLayerOreData(3, EnumMaterials.Gold, 0.32f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f,
								EnumMaterials.Uraninite, 0.15f)
						.addLayerOreData(2, EnumMaterials.Gold, 0.32f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f,
								EnumMaterials.Uraninite, 0.15f)
				.addLayerOreData(1, EnumMaterials.Gold, 0.32f,
									EnumMaterials.Hematite, 0.38f,
									EnumMaterials.Cuprite, 0.35f,
									EnumMaterials.Uraninite, 0.15f).build());

		addBiomeData(IGBiomes.PLAINS,
				//Plains
				BiomeLayerBuilder.create(IGBiomes.PLAINS).addLayerData(KAOLINITE, KAOLINITE, KAOLINITE, KAOLINITE, KAOLINITE, KAOLINITE, KAOLINITE, KAOLINITE, KAOLINITE, LIMESTONE,DOLOMITE, GRANITE, GABBROS, BASALT)
						.addLayerOreData(4, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f)
						.addLayerOreData(3, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f)
						.addLayerOreData(2, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f)
						.addLayerOreData(1, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f).build());
	}

	private void buildOceanData() {
		addBiomeData(IGBiomes.OCEAN,
				BiomeLayerBuilder.create(IGBiomes.OCEAN).addLayerData(LIMESTONE, LIMESTONE, LIMESTONE, GABBROS, GABBROS, BASALT)
				.addLayerOreData(3, EnumMaterials.Galena, 0.55f,
									EnumMaterials.Cobaltite, 0.5f,
									EnumMaterials.Sphalerite, 0.5f,
									EnumMaterials.Vanadinite, 0.5f,
									EnumMaterials.Platinum, 0.4f,
									EnumMaterials.Cuprite, 0.3f,
									EnumMaterials.Gold, 0.4f)
				.addLayerOreData(2, EnumMaterials.Galena, 0.5f,					//some lead
									EnumMaterials.Cobaltite, 0.63f,				//large amount of cobolt
									EnumMaterials.Platinum, 0.35f,				//small amount of native platinum
									EnumMaterials.Vanadinite, 0.44f,			//Vanadium
									EnumMaterials.Gold, 0.3f)
				.addLayerOreData(1, EnumMaterials.Ullmannite, 0.50f, 			//nickle
									EnumMaterials.Cuprite, 0.53f, 		   		//copper
									EnumMaterials.Cobaltite, 0.43f, 			//cobolt
									EnumMaterials.Pyrolusite, 0.43f,			//Manganese
									EnumMaterials.Gold, 0.33f).build());
		addBiomeData(IGBiomes.OCEAN_EDGE,
				//Ocean Edge
				BiomeLayerBuilder.create(IGBiomes.OCEAN_EDGE).addLayerData(LIMESTONE, LIMESTONE, LIMESTONE, GABBROS, GABBROS, BASALT)
				.addLayerOreData(3, EnumMaterials.Galena, 0.55f,
									EnumMaterials.Cobaltite, 0.55f,
									EnumMaterials.Sphalerite, 0.56f,
									EnumMaterials.Vanadinite, 0.44f,
									EnumMaterials.Platinum, 0.27f,
									EnumMaterials.Cuprite, 0.43f,
									EnumMaterials.Gold, 0.3f)
				.addLayerOreData(2, EnumMaterials.Galena, 0.55f,					//some lead
									EnumMaterials.Cobaltite, 0.55f,				//large amount of cobolt
									EnumMaterials.Platinum, 0.27f,				//small amount of native platinum
									EnumMaterials.Vanadinite, 0.44f,
									EnumMaterials.Gold, 0.3f)					//Vanadium
				.addLayerOreData(1, EnumMaterials.Ullmannite, 0.46f, 			//nickle
									EnumMaterials.Cuprite, 0.33f, 		   		//copper
									EnumMaterials.Cobaltite, 0.5f,
									EnumMaterials.Platinum, 0.3f,    			//platinum
									EnumMaterials.Pyrolusite, 0.53f,			//Manganese
									EnumMaterials.Gold, 0.33f).build());

		addBiomeData(IGBiomes.DEEP_OCEAN,
				BiomeLayerBuilder.create(IGBiomes.DEEP_OCEAN).addLayerData(LIMESTONE, GABBROS, GABBROS, GABBROS, BASALT)
				.addLayerOreData(2, EnumMaterials.Galena, 0.4f,					//some lead
									EnumMaterials.Cobaltite, 0.53f,				//large amount of cobolt
									EnumMaterials.Platinum, 0.24f,				//small amount of native platinum
									EnumMaterials.Vanadinite, 0.54f,			//small-moderate amount of Vanadium
									EnumMaterials.Gold, 0.1f)
				.addLayerOreData(1, EnumMaterials.Ullmannite, 0.50f, 			//nickle
									EnumMaterials.Cuprite, 0.33f, 		   		//copper
									EnumMaterials.Cobaltite, 0.43f, 			//cobolt
									EnumMaterials.Pyrolusite, 0.53f,			//Manganese
									EnumMaterials.Gold, 0.34f).build());            //decent amount of gold

				//Deep Volcanic Ocean
				//We know that this is going to be a low biome, only need to tell it to generate two layers,
				//we cut it in half remove some work for the ore generator
		addBiomeData(IGBiomes.DEEP_OCEAN_VOLCANIC,
				BiomeLayerBuilder.create(IGBiomes.DEEP_OCEAN_VOLCANIC).addLayerData(BASALT,BASALT)
						.addLayerOreData(1, EnumMaterials.Gold, 0.3f,
								EnumMaterials.Silver, 0.32f,
								EnumMaterials.Galena, 0.28f));
	}

	public void buildMountainData() {
		addBiomeData(IGBiomes.MOUNTAINS,
				BiomeLayerBuilder.create(IGBiomes.MOUNTAINS).addLayerData(LIMESTONE, LIMESTONE, MARBLE, RHYOLITE, BASALT)
						.addLayerOreData(5, EnumMaterials.Pyrite, 0.62f)
						.addLayerOreData(4, EnumMaterials.Pyrite, 0.42f,
								EnumMaterials.Wolframite, 0.3f)
						.addLayerOreData(3, EnumMaterials.Casserite, 0.32f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f,
								EnumMaterials.Wolframite, 0.25f)
						.addLayerOreData(2, EnumMaterials.Casserite, 0.32f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f,
								EnumMaterials.Wolframite, 0.15f)
						.addLayerOreData(1, EnumMaterials.Gold, 0.32f,
									EnumMaterials.Hematite, 0.38f,
									EnumMaterials.Cuprite, 0.35f,
									EnumMaterials.Uraninite, 0.15f).build());

		addBiomeData(IGBiomes.LUSH_MOUNTAINS,
				BiomeLayerBuilder.create(IGBiomes.LUSH_MOUNTAINS).addLayerData(LIMESTONE, MARBLE, MARBLE, RHYOLITE, BASALT)
				.addLayerOreData(5,
								EnumMaterials.Magnetite, 0.1f,
								EnumMaterials.Pyrolusite, 0.1f)
				.addLayerOreData(4, EnumMaterials.Sphalerite, 0.6f,
								EnumMaterials.Magnetite, 0.32f,
								EnumMaterials.Pyrolusite, 0.42f)
				.addLayerOreData(3, EnumMaterials.Sphalerite, 0.4f,
								EnumMaterials.Magnetite, 0.3f,
								EnumMaterials.Pyrolusite, 0.4f,
								EnumMaterials.Chromite, 0.2f)
				.addLayerOreData(2, EnumMaterials.Sphalerite, 0.1f,
						EnumMaterials.Magnetite, 0.4f,
						EnumMaterials.Pyrolusite, 0.2f,
						EnumMaterials.Chromite, 0.4f)
				.addLayerOreData(1, EnumMaterials.Sphalerite, 0.32f,
									EnumMaterials.Magnetite, 0.2f,
									EnumMaterials.Pyrolusite, 0.3f,
									EnumMaterials.Chromite, 0.5f).build());

		addBiomeData(IGBiomes.FLOODED_MOUNTAINS,
				BiomeLayerBuilder.create(IGBiomes.FLOODED_MOUNTAINS).addLayerData(RHYOLITE, RHYOLITE, RHYOLITE, GRANITE, MARBLE, MARBLE, BASALT)
						.addLayerOreData(7, EnumMaterials.Gold, 0.2f,
								EnumMaterials.Hematite, 0.3f,
								EnumMaterials.Cuprite, 0.3f)
						.addLayerOreData(6, EnumMaterials.Gold, 0.2f,
								EnumMaterials.Hematite, 0.5f,
								EnumMaterials.Cuprite, 0.4f)
						.addLayerOreData(5, EnumMaterials.Gold, 0.25f,
								EnumMaterials.Hematite, 0.45f,
								EnumMaterials.Cuprite, 0.36f)
						.addLayerOreData(4, EnumMaterials.Gold, 0.45f,
								EnumMaterials.Hematite, 0.22f,
								EnumMaterials.Cuprite, 0.32f,
								EnumMaterials.Uraninite, 0.05f)
						.addLayerOreData(3, EnumMaterials.Gold, 0.3f,
								EnumMaterials.Hematite, 0.2f,
								EnumMaterials.Cuprite, 0.3f,
								EnumMaterials.Uraninite, 0.1f)
						.addLayerOreData(2, EnumMaterials.Gold, 0.5f,
								EnumMaterials.Hematite, 0.25f,
								EnumMaterials.Cuprite, 0.25f,
								EnumMaterials.Uraninite, 0.15f)
						.addLayerOreData(1, EnumMaterials.Gold, 0.72f,
								EnumMaterials.Hematite, 0.3f,
								EnumMaterials.Cuprite, 0.2f,
								EnumMaterials.Uraninite, 0.25f)
						.build());
	}

}
