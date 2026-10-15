package com.gurizadadointerior.clih.sleep;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.gurizadadointerior.clih.identity.application.CurrentUserProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.gurizadadointerior.clih.shared.error.ApiException;
import com.gurizadadointerior.clih.shared.error.ErrorCode;

@RestController
@RequestMapping("/api/v1/sleep")
public class SleepController {
 private final JdbcTemplate jdbc;
 private final CurrentUserProvider users;
 public SleepController(JdbcTemplate jdbc, CurrentUserProvider users) { this.jdbc=jdbc; this.users=users; }
 public record Entry(LocalDate date, String sleptAt, String wokeAt, String quality, int durationMinutes) {}
 public record Input(@NotNull LocalDate date,
   @NotBlank @Pattern(regexp="^([01]\\d|2[0-3]):[0-5]\\d$") String sleptAt,
   @NotBlank @Pattern(regexp="^([01]\\d|2[0-3]):[0-5]\\d$") String wokeAt,
   @NotBlank @Pattern(regexp="Ruim|Regular|Boa|Ótima") String quality) {}
 @GetMapping
 public List<Entry> list(@RequestParam LocalDate from, @RequestParam LocalDate to) {
  if (to.isBefore(from) || ChronoUnit.DAYS.between(from,to)>366) throw new ApiException(ErrorCode.VALIDATION_ERROR,"Intervalo inválido");
  return jdbc.query("SELECT sleep_date,slept_at,woke_at,quality,duration_minutes FROM sleep_entries WHERE user_id=? AND sleep_date BETWEEN ? AND ? ORDER BY sleep_date", (rs,n)->new Entry(rs.getDate(1).toLocalDate(),rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5)), users.getCurrentUser().getId(),from,to);
 }
 @PutMapping
 @Transactional
 public Entry save(@Valid @RequestBody Input input) {
  var user=users.getCurrentUser();
  if(input.date().isAfter(LocalDate.now(java.time.ZoneId.of(user.getTimezoneId())))) throw new ApiException(ErrorCode.VALIDATION_ERROR,"A data não pode estar no futuro");
  int minutes=(int)ChronoUnit.MINUTES.between(LocalTime.parse(input.sleptAt()),LocalTime.parse(input.wokeAt()));
  if(minutes<0) minutes+=1440;
  if(minutes==0) throw new ApiException(ErrorCode.VALIDATION_ERROR,"Os horários devem ser diferentes");
  jdbc.update("INSERT INTO sleep_entries(user_id,sleep_date,slept_at,woke_at,quality,duration_minutes) VALUES (?,?,?,?,?,?) ON CONFLICT(user_id,sleep_date) DO UPDATE SET slept_at=EXCLUDED.slept_at,woke_at=EXCLUDED.woke_at,quality=EXCLUDED.quality,duration_minutes=EXCLUDED.duration_minutes",user.getId(),input.date(),input.sleptAt(),input.wokeAt(),input.quality(),minutes);
  return new Entry(input.date(),input.sleptAt(),input.wokeAt(),input.quality(),minutes);
 }
}
