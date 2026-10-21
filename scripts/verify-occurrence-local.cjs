const assert=require('node:assert/strict');
const base='http://127.0.0.1:18082/api/v1';
async function api(path,method='GET',body,headers={}) {const r=await fetch(base+path,{method,headers:{'Content-Type':'application/json',...headers},...(body?{body:JSON.stringify(body)}:{})});if(!r.ok)throw new Error(`${method} ${path}: ${r.status} ${await r.text()}`);return r.status===204?null:r.json();}
(async()=>{
 await api('/me','PATCH',{displayName:'Teste',username:'turnos_test',timezoneId:'UTC',completeOnboarding:true});
 const area=await api('/areas','POST',{name:'Cancelamento '+Date.now(),color:'#123456',icon:'book'});
 const baseline=await api('/today');const date=baseline.date;const next=new Date(date+'T12:00:00Z');next.setUTCDate(next.getUTCDate()+7);const future=next.toISOString().slice(0,10);
 const days=['SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'];
 const h=await api('/habits','POST',{areaId:area.id,name:'Excluir só uma ocorrência',measurementType:'COUNT',targetValue:1,dayPeriod:'MORNING',dayPeriods:['MORNING','EVENING'],scheduledDays:days,startDate:'2026-01-01'});
 const rows=s=>s.habits.filter(x=>x.id===h.id);
 const m=await api('/habits/'+h.id+'/completions','POST',{dayPeriod:'MORNING'},{'Idempotency-Key':'morning-'+h.id});
 const e=await api('/habits/'+h.id+'/completions','POST',{dayPeriod:'EVENING'},{'Idempotency-Key':'evening-'+h.id});
 const route='/habits/'+h.id+'/cancellations/'+date+'/MORNING';
 await api(route,'PUT');let s=await api('/today');assert.equal(rows(s).length,1);assert.equal(rows(s)[0].dayPeriod,'EVENING');assert.equal(rows(s)[0].completed,true);assert.equal(s.progress.totalXp,baseline.progress.totalXp+e.xpAwarded);
 await api(route,'PUT');assert.equal((await api('/today')).progress.totalXp,s.progress.totalXp);
 assert.equal(rows(await api('/today?date='+future)).length,2);
 await api(route,'DELETE');s=await api('/today');assert.equal(rows(s).length,2);assert.equal(rows(s).find(x=>x.dayPeriod==='MORNING').completed,false);
 const yesterday=new Date(date+'T12:00:00Z');yesterday.setUTCDate(yesterday.getUTCDate()-1);const past=yesterday.toISOString().slice(0,10);
 await api('/habits/'+h.id+'/cancellations/'+past+'/MORNING','PUT');assert.equal(rows(await api('/today?date='+past)).length,1);
 await api('/habits/'+h.id,'DELETE');
 console.log('PASS: selected date and period only; completed occurrence undoes XP; repeat cancellation idempotent; next week intact; undo restores pending; past date supported.');
})().catch(e=>{console.error(e);process.exitCode=1});
