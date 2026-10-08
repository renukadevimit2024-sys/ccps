let customers = [];
let transactions = [];
let custSeq = 1, txSeq = 1000;

function seedData(){
  customers = [
    {id:1, name:"Renuka Devi M", email:"renuka@example.com", phone:"9876543210"},
    {id:2, name:"Arjun Menon", email:"arjun@example.com", phone:"9845123456"}
  ];
  custSeq = 3;
  transactions = [
    {id:1001, custId:1, card:"**** 1111", amount:250.00, status:"Approved", date:"2026-09-20"},
    {id:1002, custId:2, card:"**** 4242", amount:75.50, status:"Approved", date:"2026-09-21"},
    {id:1003, custId:1, card:"**** 9999", amount:610.00, status:"Declined", date:"2026-09-22"}
  ];
  txSeq = 1004;
}

function login(){
  const u = document.getElementById('li-user').value.trim();
  const p = document.getElementById('li-pass').value.trim();
  const err = document.getElementById('login-err');
  if(!u || !p){ err.classList.add('show'); return; }
  err.classList.remove('show');
  document.getElementById('login-view').style.display = 'none';
  document.getElementById('main-view').style.display = 'flex';
  seedData();
  refreshAll();
}
function logout(){
  document.getElementById('main-view').style.display = 'none';
  document.getElementById('login-view').style.display = 'flex';
}

document.querySelectorAll('.nav-item[data-page]').forEach(item=>{
  item.addEventListener('click', ()=>{
    document.querySelectorAll('.nav-item[data-page]').forEach(i=>i.classList.remove('active'));
    item.classList.add('active');
    document.querySelectorAll('.page').forEach(p=>p.classList.remove('active'));
    document.getElementById('page-'+item.dataset.page).classList.add('active');
    if(item.dataset.page === 'reports') renderReports();
    if(item.dataset.page === 'payment') renderCustomerOptions();
  });
});

function addCustomer(){
  const name = document.getElementById('c-name').value.trim();
  const email = document.getElementById('c-email').value.trim();
  const phone = document.getElementById('c-phone').value.trim();
  if(!name || !email){ return; }
  customers.push({id:custSeq++, name, email, phone});
  document.getElementById('c-name').value='';
  document.getElementById('c-email').value='';
  document.getElementById('c-phone').value='';
  const msg = document.getElementById('c-msg');
  msg.classList.add('show');
  setTimeout(()=>msg.classList.remove('show'), 2500);
  renderCustomers();
}

function luhnValid(num){
  const digits = num.replace(/\D/g,'');
  if(digits.length < 13) return false;
  let sum = 0, alt = false;
  for(let i = digits.length-1; i>=0; i--){
    let n = parseInt(digits[i],10);
    if(alt){ n*=2; if(n>9) n-=9; }
    sum += n; alt = !alt;
  }
  return sum % 10 === 0;
}
function expiryValid(exp){
  const m = exp.match(/^(\d{2})\/(\d{2})$/);
  if(!m) return false;
  const month = parseInt(m[1],10), year = 2000+parseInt(m[2],10);
  if(month<1||month>12) return false;
  const now = new Date();
  const expDate = new Date(year, month, 0);
  return expDate >= now;
}

function processPayment(){
  const custId = parseInt(document.getElementById('p-customer').value,10);
  const amount = parseFloat(document.getElementById('p-amount').value);
  const cardnum = document.getElementById('p-cardnum').value;
  const exp = document.getElementById('p-exp').value;
  const cvv = document.getElementById('p-cvv').value;
  const msg = document.getElementById('p-msg');

  let reasons = [];
  if(!custId) reasons.push('select a customer');
  if(!amount || amount<=0) reasons.push('enter a valid amount');
  if(!luhnValid(cardnum)) reasons.push('invalid card number');
  if(!expiryValid(exp)) reasons.push('invalid or expired card');
  if(!cvv || cvv.length<3) reasons.push('invalid CVV');

  const last4 = cardnum.replace(/\D/g,'').slice(-4) || '0000';
  let status;
  if(reasons.length){
    status = 'Declined';
  } else {
    status = amount > 500 ? 'Declined' : 'Approved';
    if(status === 'Declined') reasons.push('amount exceeds authorization limit');
  }

  transactions.unshift({
    id: txSeq++, custId, card: '**** '+last4, amount: amount||0, status,
    date: new Date().toISOString().slice(0,10)
  });

  msg.className = 'msg show ' + (status==='Approved' ? 'ok' : 'bad');
  msg.textContent = status==='Approved'
    ? 'Payment authorized and processed successfully.'
    : 'Payment declined — ' + reasons.join(', ') + '.';

  refreshAll();
}

function renderCustomerOptions(){
  const sel = document.getElementById('p-customer');
  sel.innerHTML = customers.map(c=>`<option value="${c.id}">${c.name}</option>`).join('');
}
function renderCustomers(){
  document.getElementById('customer-list').innerHTML = customers.map(c=>
    `<tr><td>${c.id}</td><td>${c.name}</td><td>${c.email}</td><td>${c.phone||'-'}</td></tr>`).join('');
}
function custName(id){
  const c = customers.find(c=>c.id===id);
  return c ? c.name : 'Unknown';
}
function renderTransactions(){
  const search = (document.getElementById('t-search').value||'').toLowerCase();
  const status = document.getElementById('t-status').value;
  const rows = transactions.filter(t=>{
    const name = custName(t.custId).toLowerCase();
    const matchSearch = !search || name.includes(search) || String(t.id).includes(search);
    const matchStatus = !status || t.status===status;
    return matchSearch && matchStatus;
  });
  document.getElementById('tx-list').innerHTML = rows.map(t=>
    `<tr><td>#${t.id}</td><td>${custName(t.custId)}</td><td>${t.card}</td><td>$${t.amount.toFixed(2)}</td>
    <td><span class="badge ${t.status==='Approved'?'badge-ok':'badge-bad'}">${t.status}</span></td><td>${t.date}</td></tr>`).join('');
}
function renderHome(){
  document.getElementById('stat-customers').textContent = customers.length;
  document.getElementById('stat-tx').textContent = transactions.length;
  const approved = transactions.filter(t=>t.status==='Approved');
  document.getElementById('stat-approved').textContent = approved.length;
  const revenue = approved.reduce((s,t)=>s+t.amount,0);
  document.getElementById('stat-revenue').textContent = '$'+revenue.toFixed(2);
  document.getElementById('home-recent').innerHTML = transactions.slice(0,5).map(t=>
    `<tr><td>#${t.id}</td><td>${custName(t.custId)}</td><td>$${t.amount.toFixed(2)}</td>
    <td><span class="badge ${t.status==='Approved'?'badge-ok':'badge-bad'}">${t.status}</span></td></tr>`).join('');
}
function renderReports(){
  const total = transactions.length;
  const approved = transactions.filter(t=>t.status==='Approved').length;
  const declined = total - approved;
  const avg = total ? transactions.reduce((s,t)=>s+t.amount,0)/total : 0;
  document.getElementById('r-total').textContent = total;
  document.getElementById('r-approved').textContent = approved;
  document.getElementById('r-declined').textContent = declined;
  document.getElementById('r-avg').textContent = '$'+avg.toFixed(2);

  const max = Math.max(approved, declined, 1);
  document.getElementById('report-bars').innerHTML = `
    <div class="bar-wrap"><div class="bar" style="height:${(approved/max)*120}px;background:var(--ok);"></div><div class="bar-lbl">Approved (${approved})</div></div>
    <div class="bar-wrap"><div class="bar" style="height:${(declined/max)*120}px;background:var(--bad);"></div><div class="bar-lbl">Declined (${declined})</div></div>
  `;
}
function refreshAll(){
  renderCustomers();
  renderCustomerOptions();
  renderTransactions();
  renderHome();
}
