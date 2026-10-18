// Run only against an isolated local database with the backend's local profile.
const assert = require('node:assert/strict');
const base = 'http://127.0.0.1:18082/api/v1';
async function api(path,method='GET',body,headers={}) {
 const r=await fetch(base+path,{method,headers:{'Content-Type':'application/json',...headers},...(body?{body:JSON.stringify(body)}:{})});
 if(!r.ok) throw new Error(`${method} ${path}: ${r.status} ${await r.text()}`);
 return r.status===204?null:r.json();
}
(async()=>{
 const bad=await fetch(base+'/me',{method:'PATCH',headers:{'Content-Type':'application/json'},body:JSON.stringify({displayName:'Teste',username:'',completeOnboarding:true})});
 assert.equal(bad.status,400);
 await api('/me','PATCH',{displayName:'Teste',username:'turnos_test',timezoneId:'UTC',completeOnboarding:true});
 const area=await api('/areas','POST',{name:'Teste de turnos '+Date.now(),color:'#123456',icon:'book'});
 const weekdays=['SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'];
 const weekday=new Date().getUTCDay();
 const days=[0,2,4].map(offset=>weekdays[(weekday+offset)%7]);
 const habit=await api('/habits','POST',{areaId:area.id,name:'Recorrência por turno',measurementType:'COUNT',targetValue:1,dayPeriod:'MORNING',dayPeriods:['MORNING','EVENING'],scheduledDays:days});
 let summary=await api('/today');
 const current=summary.date;
 const baselineCompleted=summary.summary.completed; const baselineXp=summary.progress.totalXp;
 const dayNames=['SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'];
 for(let offset=0;offset<35;offset++) {
  const date=new Date(current+'T12:00:00Z');date.setUTCDate(date.getUTCDate()+offset);
  const result=await api('/today?date='+date.toISOString().slice(0,10));
  assert.equal(result.habits.filter(h=>h.id===habit.id).length,days.includes(dayNames[date.getUTCDay()])?2:0);
 }
 const morning=await api('/habits/'+habit.id+'/completions','POST',{dayPeriod:'MORNING'},{'Idempotency-Key':'verify-morning-'+habit.id});
 summary=await api('/today');
 assert.equal(summary.habits.find(h=>h.id===habit.id && h.dayPeriod==='MORNING').completed,true);
 assert.equal(summary.habits.find(h=>h.id===habit.id && h.dayPeriod==='EVENING').completed,false);
 const evening=await api('/habits/'+habit.id+'/completions','POST',{dayPeriod:'EVENING'},{'Idempotency-Key':'verify-evening-'+habit.id});
 assert.notEqual(morning.id,evening.id);
 summary=await api('/today');assert.equal(summary.summary.completed,baselineCompleted+2);
 assert.equal(summary.progress.totalXp,baselineXp+morning.xpAwarded+evening.xpAwarded);
 await api('/habits/'+habit.id+'/completions/'+morning.id,'DELETE');
 summary=await api('/today');
 assert.equal(summary.habits.find(h=>h.id===habit.id && h.dayPeriod==='MORNING').completed,false);
 assert.equal(summary.habits.find(h=>h.id===habit.id && h.dayPeriod==='EVENING').completed,true);
 assert.equal(summary.progress.totalXp,baselineXp+evening.xpAwarded);
 await api('/habits/'+habit.id,'DELETE');
 summary=await api('/today');assert.equal(summary.habits.some(h=>h.id===habit.id),false);
 console.log('PASS: weekly recurrence across 35 dates; separate completions, XP and undo; archive; mandatory username.');
})().catch(e=>{console.error(e);process.exitCode=1;});
