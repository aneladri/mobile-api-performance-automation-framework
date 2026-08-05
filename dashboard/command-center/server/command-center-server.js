'use strict';
const fs=require('fs');const path=require('path');const http=require('http');const {spawnSync}=require('child_process');
const root=path.resolve(process.argv[2]||process.cwd());const port=Number(process.env.MAPAF_COMMAND_CENTER_PORT||process.argv[3]||8098);
const mime={'.html':'text/html; charset=utf-8','.js':'text/javascript; charset=utf-8','.css':'text/css; charset=utf-8','.json':'application/json; charset=utf-8','.png':'image/png','.svg':'image/svg+xml','.jpg':'image/jpeg','.jpeg':'image/jpeg','.webp':'image/webp'};
const safe=p=>{const full=path.resolve(root,'.'+p);return full.startsWith(root)?full:null};
const discover=()=>spawnSync(process.execPath,[path.join(root,'dashboard/command-center/discovery/discover-modules.js'),root],{stdio:'ignore'});
const send=(res,status,body,type='application/json; charset=utf-8')=>{res.writeHead(status,{'Content-Type':type,'Cache-Control':'no-store','Access-Control-Allow-Origin':'*'});res.end(body)};
const server=http.createServer((req,res)=>{
 const url=new URL(req.url,'http://localhost');
 if(url.pathname==='/api/command-center/modules'){discover();const p=path.join(root,'reports/command-center/modules.json');return send(res,fs.existsSync(p)?200:500,fs.existsSync(p)?fs.readFileSync(p):JSON.stringify({error:'discovery failed'}));}
 if(url.pathname==='/api/command-center/health')return send(res,200,JSON.stringify({status:'UP',service:'mapaf-command-center',root,port}));
 let pathname=url.pathname==='/'?'/dashboard/command-center/app/index.html':url.pathname;
 const file=safe(pathname); if(!file||!fs.existsSync(file)||fs.statSync(file).isDirectory())return send(res,404,JSON.stringify({error:'not_found',path:pathname}));
 send(res,200,fs.readFileSync(file),mime[path.extname(file)]||'application/octet-stream');
});
server.listen(port,'0.0.0.0',()=>console.log(`MAPAF Command Center Application: http://localhost:${port}/`));
