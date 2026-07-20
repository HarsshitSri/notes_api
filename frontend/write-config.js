const { writeFileSync } = require("fs");
const { join } = require("path");

const base = (process.env.NOTES_API_BASE || "").replace(/\/$/, "");
const out = join(__dirname, "config.js");
writeFileSync(out, `window.NOTES_API_BASE = ${JSON.stringify(base)};\n`);
console.log(`Wrote config.js with NOTES_API_BASE=${JSON.stringify(base)}`);
