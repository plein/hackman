package player;/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

/**
 * player.Player
 *
 * Stores all information about a player
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class Player {

    private String name;
    private boolean hasWeapon;
    private boolean isParalyzed;
    private int snippets;

    public Player(String playerName) {
        this.name = playerName;
    }

    public void setWeapon(boolean hasWeapon) {
        this.hasWeapon = hasWeapon;
    }

    public void setParalyzed(boolean isParalyzed) {
        this.isParalyzed = isParalyzed;
    }

    public void setSnippets(int snippets) {
        this.snippets = snippets;
    }

    public boolean isParalyzed() {
        return this.isParalyzed;
    }

    public int getSnippets() {
        return this.snippets;
    }

    public boolean hasWeapon() {
        return this.hasWeapon;
    }

    public String getName() {
        return this.name;
    }
}
