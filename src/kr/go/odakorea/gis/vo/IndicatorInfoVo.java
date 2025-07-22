package kr.go.odakorea.gis.vo;

public class IndicatorInfoVo {
	private String code;
	private String name;
	private String description;
	private String unit;
	private Double target;
	private Double threshold;
	private boolean higherIsBetter; // true면 높을수록 좋음, false면 낮을수록 좋음
	
    public IndicatorInfoVo() {
    }
    
	public IndicatorInfoVo(String code, String name, String description, String unit, 
			Double target, Double badThreshold, boolean higherIsBetter) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.unit = unit;
		this.target = target;
		this.threshold = threshold;
		this.higherIsBetter = higherIsBetter;
	}
	
	public Double getTarget() {
		return target;
	}

	public void setTarget(Double target) {
		this.target = target;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public String evaluateValue(Double value) {
		if (value == null) return "데이터 없음";
		
		if (higherIsBetter) {
			if (value >= target) return "좋음";
			else if (value <= threshold) return "나쁨";
			else return "보통";
		} else {
			if (value <= target) return "좋음";
			else if (value >= threshold) return "나쁨";
			else return "보통";
		}
	}
	
	public String getCriteriaExplanation() {
		if (higherIsBetter) {
			return String.format("기준: %s 이상(좋음), %s 이하(나쁨), 그 사이(보통)", 
					target, threshold);
		} else {
			return String.format("기준: %s 이하(좋음), %s 이상(나쁨), 그 사이(보통)", 
					target, threshold);
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	

	public boolean isHigherIsBetter() {
		return higherIsBetter;
	}

	public void setHigherIsBetter(boolean higherIsBetter) {
		this.higherIsBetter = higherIsBetter;
	}
	
	
    
}
