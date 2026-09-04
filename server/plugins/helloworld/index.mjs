export async function greet(args) {
  const name = args?.name ?? 'friend';
  const now = new Date().toLocaleTimeString();
  return `👋 Hello ${name}! Greetings from the VirgoYT cloud at ${now}.`;
}