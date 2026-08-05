'use strict';
(() => {
  async function api(path, options={}) {
    const response = await fetch(path, { headers:{'Content-Type':'application/json',...(options.headers||{})}, ...options });
    const raw = await response.text(); let body={}; try { body=raw?JSON.parse(raw):{}; } catch { body={raw}; }
    if (!response.ok) { const error=new Error(body.message||body.error||`Request failed: ${response.status}`); error.status=response.status; error.body=body; throw error; }
    return body;
  }
  const el = id => document.querySelector(`[data-testid="${id}"]`);
  const text = (id, value) => { const n=el(id); if(n)n.textContent=value; };
  const value = (id, v) => { const n=el(id); if(n)n.value=v??''; };
  const show = (id, visible=true) => { const n=el(id); if(n)n.classList.toggle('hidden',!visible); };
  const status = (id, value, kind='info') => { const n=el(id); if(n){n.textContent=value;n.className=`banner banner-${kind}`;} };
  window.Updr={api,el,text,value,show,status};
})();
