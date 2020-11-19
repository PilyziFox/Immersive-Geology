package com.igteam.immersivegeology.api.materials.material_bases;

import com.igteam.immersivegeology.api.interfaces.IHeadMaterial;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialTypes;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

import javax.annotation.Nullable;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public abstract class MaterialStoneBase extends Material implements IHeadMaterial
{
	public abstract EnumStoneType getStoneType();

	@Override
	public boolean hasUsetype(MaterialUseType useType)
	{
		switch(useType)
		{
			//items
			case ROCK:
				//blocks
			case SPIKE:
			case ORE_BEARING:
			case ORE_CHUNK:
			case POLISHED_CHUNK:
			case GRAVEL:
			case COBBLESTONE:
			case POLISHED_STONE:
			case MOSS_ROCK:
			case SMALL_BRICKS:
			case NORMAL_BRICKS:
			case ROAD_BRICKS:
			case ROUGH_BRICKS:
			case PILLAR:
			case COLUMN:
			case TILES:
			case CHUNK:
			case ROCK_BIT:
			case ORE_BIT:
				return true;
		}
		return false;
	}

	@Nullable
	@Override
	public String getSpecialSubtypeModelName(MaterialUseType useType)
	{
		if(useType==MaterialUseType.ORE_BEARING||useType==MaterialUseType.ROCK||useType==MaterialUseType.MOSS_ROCK)
			return getStoneType().getName();
		return null;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.STONE;
	}
	
	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.IRON;
	}

	public enum EnumStoneType
	{
		IGNEOUS_INTRUSIVE,
		IGNEOUS_EXTRUSIVE,
		METAMORPHIC,
		SEDIMENTARY;

		public String getName()
		{
			return toString().toLowerCase();
		}
	}
}