/*
# Create game_saves table (single-tenant, no auth)

1. New Tables
- `game_saves`
  - `id` (uuid, primary key, auto-generated)
  - `shinobi_name` (text, name of the shinobi character)
  - `clan` (text, one of: Uzumaki, Uchiha, Hyuga)
  - `age` (int, current age of the shinobi, 6-50)
  - `rating` (int, shinobi rating, 20-100)
  - `skill_points` (int, unspent skill points available to allocate)
  - `clan_skills` (jsonb, clan-specific skill tree progress, e.g. {"skillId": level})
  - `elemental_skills` (jsonb, elemental skill tree progress, e.g. {"fire": level, "water": level})
  - `created_at` (timestamptz, when the save was created)
  - `updated_at` (timestamptz, when the save was last modified)

2. Security
- Enable RLS on `game_saves`.
- Allow anon + authenticated CRUD because this is a single-player game with no sign-in.
- All data is intentionally public/shared across the single-tenant app.
*/

CREATE TABLE IF NOT EXISTS game_saves (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  shinobi_name text NOT NULL,
  clan text NOT NULL,
  age integer NOT NULL DEFAULT 6,
  rating integer NOT NULL DEFAULT 20,
  skill_points integer NOT NULL DEFAULT 0,
  clan_skills jsonb NOT NULL DEFAULT '{}'::jsonb,
  elemental_skills jsonb NOT NULL DEFAULT '{}'::jsonb,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

ALTER TABLE game_saves ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "anon_select_game_saves" ON game_saves;
CREATE POLICY "anon_select_game_saves" ON game_saves FOR SELECT
TO anon, authenticated USING (true);

DROP POLICY IF EXISTS "anon_insert_game_saves" ON game_saves;
CREATE POLICY "anon_insert_game_saves" ON game_saves FOR INSERT
TO anon, authenticated WITH CHECK (true);

DROP POLICY IF EXISTS "anon_update_game_saves" ON game_saves;
CREATE POLICY "anon_update_game_saves" ON game_saves FOR UPDATE
TO anon, authenticated USING (true) WITH CHECK (true);

DROP POLICY IF EXISTS "anon_delete_game_saves" ON game_saves;
CREATE POLICY "anon_delete_game_saves" ON game_saves FOR DELETE
TO anon, authenticated USING (true);