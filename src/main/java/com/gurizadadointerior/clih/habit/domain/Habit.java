package com.gurizadadointerior.clih.habit.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.gurizadadointerior.clih.shared.persistence.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "habits")
public class Habit extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "area_id", nullable = false)
	private UUID areaId;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(length = 500)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "measurement_type", nullable = false, length = 30)
	private MeasurementType measurementType;

	@Column(name = "target_value", nullable = false, precision = 12, scale = 2)
	private BigDecimal targetValue;

	@Column(length = 30)
	private String unit;

	@Enumerated(EnumType.STRING)
	@Column(name = "day_period", nullable = false, length = 20)
	private DayPeriod dayPeriod;

	@Column(name = "preferred_time")
	private LocalTime preferredTime;

	@Column(nullable = false)
	private int position;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(nullable = false)
	private boolean active;

	@Column(name = "archived_at")
	private Instant archivedAt;

	@Version
	@Column(nullable = false)
	private long version;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "habit_schedules", joinColumns = @JoinColumn(name = "habit_id"))
	@Column(name = "day_of_week", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Set<DayOfWeek> scheduledDays = new HashSet<>();

	protected Habit() {
	}

	public Habit(
		UUID userId,
		UUID areaId,
		String name,
		String description,
		MeasurementType measurementType,
		BigDecimal targetValue,
		String unit,
		DayPeriod dayPeriod,
		LocalTime preferredTime,
		int position,
		LocalDate startDate,
		LocalDate endDate,
		Set<DayOfWeek> scheduledDays
	) {
		this.userId = userId;
		this.areaId = areaId;
		this.name = name;
		this.description = description;
		this.measurementType = measurementType;
		this.targetValue = targetValue;
		this.unit = unit;
		this.dayPeriod = dayPeriod;
		this.preferredTime = preferredTime;
		this.position = position;
		this.startDate = startDate;
		this.endDate = endDate;
		this.scheduledDays = new HashSet<>(scheduledDays);
		this.active = true;
	}

	public void update(
		UUID areaId,
		String name,
		String description,
		MeasurementType measurementType,
		BigDecimal targetValue,
		String unit,
		DayPeriod dayPeriod,
		LocalTime preferredTime,
		Integer position,
		LocalDate startDate,
		LocalDate endDate,
		Set<DayOfWeek> scheduledDays,
		Boolean active
	) {
		if (areaId != null) this.areaId = areaId;
		if (name != null) this.name = name;
		if (description != null) this.description = description;
		if (measurementType != null) this.measurementType = measurementType;
		if (targetValue != null) this.targetValue = targetValue;
		if (unit != null) this.unit = unit;
		if (dayPeriod != null) this.dayPeriod = dayPeriod;
		if (preferredTime != null) this.preferredTime = preferredTime;
		if (position != null) this.position = position;
		if (startDate != null) this.startDate = startDate;
		if (endDate != null) this.endDate = endDate;
		if (scheduledDays != null) this.scheduledDays = new HashSet<>(scheduledDays);
		if (active != null) this.active = active;
	}

	public void archive(Instant archivedAt) {
		this.archivedAt = archivedAt;
		this.active = false;
	}

	public boolean isScheduledFor(LocalDate date) {
		return active
			&& archivedAt == null
			&& !date.isBefore(startDate)
			&& (endDate == null || !date.isAfter(endDate))
			&& scheduledDays.contains(date.getDayOfWeek());
	}

	public UUID getUserId() { return userId; }
	public UUID getAreaId() { return areaId; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public MeasurementType getMeasurementType() { return measurementType; }
	public BigDecimal getTargetValue() { return targetValue; }
	public String getUnit() { return unit; }
	public DayPeriod getDayPeriod() { return dayPeriod; }
	public LocalTime getPreferredTime() { return preferredTime; }
	public int getPosition() { return position; }
	public LocalDate getStartDate() { return startDate; }
	public LocalDate getEndDate() { return endDate; }
	public boolean isActive() { return active; }
	public Instant getArchivedAt() { return archivedAt; }
	public Set<DayOfWeek> getScheduledDays() { return Set.copyOf(scheduledDays); }
}
