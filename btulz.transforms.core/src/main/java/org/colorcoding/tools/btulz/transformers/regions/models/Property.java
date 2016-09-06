package org.colorcoding.tools.btulz.transformers.regions.models;

import java.util.ArrayList;
import java.util.List;

import org.colorcoding.tools.btulz.Environment;
import org.colorcoding.tools.btulz.models.IProperty;
import org.colorcoding.tools.btulz.models.data.emDataSubType;
import org.colorcoding.tools.btulz.models.data.emDataType;
import org.colorcoding.tools.btulz.models.data.emYesNo;
import org.colorcoding.tools.btulz.templates.Parameter;

public class Property implements IProperty {
	/**
	 * 数据库字段映射类型
	 */
	public static final String PARAMETER_NAME_MAPPED_TYPE = "MappedType";
	/**
	 * 开发语言属性映射类型
	 */
	public static final String PARAMETER_NAME_DECLARED_TYPE = "DeclaredType";

	public Property(org.colorcoding.tools.btulz.models.IProperty property) {
		this.property = property;
	}

	private IProperty property;

	@Override
	public String getName() {
		return this.property.getName();
	}

	@Override
	public void setName(String name) {
		this.property.setName(name);
	}

	@Override
	public String getDescription() {
		return this.property.getDescription();
	}

	@Override
	public void setDescription(String description) {
		this.property.setDescription(description);
	}

	@Override
	public void setDeclaredType(String declaredType) {
		this.property.setDeclaredType(declaredType);
	}

	@Override
	public emYesNo isPrimaryKey() {
		return this.property.isPrimaryKey();
	}

	@Override
	public void setPrimaryKey(emYesNo value) {
		this.property.setPrimaryKey(value);
	}

	@Override
	public emYesNo isUniqueKey() {
		return this.property.isUniqueKey();
	}

	@Override
	public void setUniqueKey(emYesNo value) {
		this.property.setUniqueKey(value);
	}

	@Override
	public emDataType getDataType() {
		return this.property.getDataType();
	}

	@Override
	public void setDataType(emDataType dataType) {
		this.property.setDataType(dataType);
	}

	@Override
	public emDataSubType getDataSubType() {
		return this.property.getDataSubType();
	}

	@Override
	public void setDataSubType(emDataSubType dataSubType) {
		this.property.setDataSubType(dataSubType);
	}

	@Override
	public int getEditSize() {
		return this.property.getEditSize();
	}

	@Override
	public void setEditSize(int editSize) {
		this.property.setEditSize(editSize);
	}

	@Override
	public String getMapped() {
		return this.property.getMapped();
	}

	@Override
	public void setMapped(String mapped) {
		this.property.setMapped(mapped);
	}

	@Override
	public IProperty clone() {
		return null;
	}

	private boolean last;

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public String toString() {
		return String.format("RegionProperty %s", this.getName());
	}

	private List<DataTypeMapping> declaredTypeMappings;

	public List<DataTypeMapping> getDeclaredTypeMappings() {
		if (this.declaredTypeMappings == null) {
			this.declaredTypeMappings = new ArrayList<>();
		}
		return declaredTypeMappings;
	}

	public void addDeclaredTypeMappings(Iterable<?> value) {
		this.getDeclaredTypeMappings().clear();
		if (value != null) {
			for (Object item : value) {
				if (item instanceof DataTypeMapping) {
					this.getDeclaredTypeMappings().add((DataTypeMapping) item);
				}
			}
		}
	}

	public void addDeclaredTypeMappings(Parameter par) {
		if (par == null) {
			return;
		}
		Object value = par.getValue();
		if (Iterable.class.isInstance(value)) {
			this.addDeclaredTypeMappings((Iterable<?>) value);
		}
	}

	@Override
	public String getDeclaredType() {
		// 优先定义
		if (this.property.getDeclaredType() != null) {
			return this.property.getDeclaredType();
		}
		// 未发现定义使用映射
		for (DataTypeMapping mapping : this.getMappedTypeMappings()) {
			if (mapping == null) {
				continue;
			}
			if (mapping.getDateType() != this.getDataType()) {
				continue;
			}
			if (mapping.getSubType() != null && mapping.getSubType() != this.getDataSubType()) {
				continue;
			}
			// 存在映射
			try {
				return mapping.getMappedType(this);
			} catch (Exception e) {
				Environment.getLogger().error(e);
			}
		}
		return this.property.getDeclaredType();
	}

	private List<DataTypeMapping> mappedTypeMappings;

	public List<DataTypeMapping> getMappedTypeMappings() {
		if (this.mappedTypeMappings == null) {
			this.mappedTypeMappings = new ArrayList<>();
		}
		return mappedTypeMappings;
	}

	public void addMappedTypeMappings(Iterable<?> value) {
		this.getMappedTypeMappings().clear();
		if (value != null) {
			for (Object item : value) {
				if (item instanceof DataTypeMapping) {
					this.getMappedTypeMappings().add((DataTypeMapping) item);
				}
			}
		}
	}

	public void addDataTypeMappings(Parameter par) {
		if (par == null) {
			return;
		}
		Object value = par.getValue();
		if (Iterable.class.isInstance(value)) {
			this.addMappedTypeMappings((Iterable<?>) value);
		}
	}

	public String getMappedType() throws Exception {
		// 优先使用映射
		for (DataTypeMapping mapping : this.getMappedTypeMappings()) {
			if (mapping == null) {
				continue;
			}
			if (mapping.getDateType() != this.getDataType()) {
				continue;
			}
			if (mapping.getSubType() != null && mapping.getSubType() != this.getDataSubType()) {
				continue;
			}
			// 存在映射
			return mapping.getMappedType(this);
		}
		// 默认类型
		switch (this.getDataType()) {
		case Memo:
			if (this.getDataSubType() == emDataSubType.Default)
				return "ntext";
		case Numeric:
			return "int";
		case Date:
			if (this.getDataSubType() == emDataSubType.Default)
				return "datetime";
			else if (this.getDataSubType() == emDataSubType.Time)
				return "smallint";
		case Decimal:
			return "numeric(19, 6)";
		case Bytes:
			return "bit";
		default:
			break;
		}
		return String.format("nvarchar(%s)", this.getEditSize());
	}

	public String getNullType() {
		if (this.isPrimaryKey() == emYesNo.Yes) {
			return "not null";
		}
		return "null";
	}

	private String annotatedType;

	public String getAnnotatedType() {
		if (this.annotatedType == null) {
			switch (this.getDataType()) {
			case Alphanumeric:
				this.annotatedType = "db_Alphanumeric";
				break;
			case Memo:
				this.annotatedType = "db_Memo";
				break;
			case Numeric:
				this.annotatedType = "db_Numeric";
				break;
			case Date:
				if (this.getDataSubType() == emDataSubType.Default)
					this.annotatedType = "db_Date";
				else if (this.getDataSubType() == emDataSubType.Time)
					this.annotatedType = "db_Numeric";
				break;
			case Decimal:
				this.annotatedType = "db_Decimal";
				break;
			case Bytes:
				this.annotatedType = "db_Bytes";
				break;
			default:
				this.annotatedType = "db_Unknown";
				break;
			}
		}
		return annotatedType;
	}

	public void setAnnotatedType(String annotatedType) {
		this.annotatedType = annotatedType;
	}

	public String getSeparator(String value) {
		return this.isLast() ? "" : value;
	}
}